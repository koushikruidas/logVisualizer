package com.poincian.logVisualizer.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import com.poincian.logVisualizer.model.LogEntryDTO;
import com.poincian.logVisualizer.model.LogSearchResponseDTO;
import com.poincian.logVisualizer.service.interfaces.LogServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogService implements LogServiceInterface {

    private final ElasticsearchClient elasticsearchClient;

    @Override
    public LogSearchResponseDTO searchLogs(String index, String level, String serviceName, String keyword,
                                           LocalDateTime startDate, LocalDateTime endDate,
                                           int page, int size) {
        try {
            BoolQuery.Builder boolQuery = QueryBuilders.bool();

            if (level != null) {
                boolQuery.must(QueryBuilders.match(m -> m.field("level").query(level)));
            }
            if (serviceName != null) {
                boolQuery.must(QueryBuilders.match(m -> m.field("serviceName").query(serviceName)));
            }
            if (keyword != null) {
                boolQuery.must(QueryBuilders.queryString(q -> q.query("*" + keyword + "*")));
            }
            if (startDate != null || endDate != null) {
                boolQuery.must(QueryBuilders.range(r -> r
                        .date(d -> {
                            if (startDate != null) d.gte(startDate.toString());
                            if (endDate != null) d.lte(endDate.toString());
                            return d;
                        })
                ));
            }


            SearchRequest request = SearchRequest.of(s -> s
                    .index(index)
                    .from(page * size)
                    .size(size)
                    .query(boolQuery.build()._toQuery())
            );

            SearchResponse<Map> response = elasticsearchClient.search(request, Map.class);

            List<LogEntryDTO> logs = response.hits().hits().stream().map(hit -> {
                Map<String, Object> source = hit.source();
                assert source != null;
                return LogEntryDTO.builder()
                        .id(hit.id())
                        .level((String) source.get("level"))
                        .serviceName((String) source.get("serviceName"))
                        .message((String) source.get("message"))
                        .timestamp(source.get("timestamp") != null
                                ? LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(source.get("timestamp").toString())), ZoneId.systemDefault())
                                : null)
                        .rawLog(source.toString())
//                        .indexName(hit.index())
//                        .metadata(source)
                        .build();
            }).collect(Collectors.toList());

            assert response.hits().total() != null;
            return new LogSearchResponseDTO(logs, response.hits().total().value());
        } catch (Exception e) {
            log.error("Error while querying Elasticsearch", e);
            return new LogSearchResponseDTO(List.of(), 0);
        }
    }
}
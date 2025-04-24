package com.poincian.logVisualizer.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import com.poincian.logVisualizer.exception.ElasticsearchException;
import com.poincian.logVisualizer.exception.IndexNotFoundException;
import com.poincian.logVisualizer.model.request.LogEntryDocumentDTO;
import com.poincian.logVisualizer.model.response.LogSearchResponseDTO;
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
            if (index == null || index.isEmpty()) {
                throw new IllegalArgumentException("Index name must not be empty.");
            }

            if (!isIndexPresent(index)) {
                throw new IndexNotFoundException("Index '" + index + "' not found in Elasticsearch.");
            }

            BoolQuery.Builder boolQuery = QueryBuilders.bool();
            // Build the query based on provided parameters
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
            // Create the search request
            SearchRequest request = SearchRequest.of(s -> s
                    .index(index)
                    .from(page * size)
                    .size(size)
                    .query(boolQuery.build()._toQuery())
            );

            SearchResponse<Map> response = elasticsearchClient.search(request, Map.class);

            if (response == null || response.hits() == null) {
                throw new ElasticsearchException("No results returned from Elasticsearch.");
            }

            List<LogEntryDocumentDTO> logs = response.hits().hits().stream().map(hit -> {
                Map<String, Object> source = hit.source();
                if (source == null) {
                    throw new ElasticsearchException("Invalid log entry data.");
                }
                return LogEntryDocumentDTO.builder()
                        .id(hit.id())
                        .level((String) source.get("level"))
                        .serviceName((String) source.get("serviceName"))
                        .message((String) source.get("message"))
                        .timestamp(source.get("timestamp") != null
                                ? LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(source.get("timestamp").toString())), ZoneId.systemDefault())
                                : null)
                        .rawLog(source.toString())
                        .build();
            }).collect(Collectors.toList());

            return LogSearchResponseDTO.builder().logs(logs)
                    .totalHits(response.hits().total() != null ? response.hits().total().value() : 0)
                    .build();
        } catch (IndexNotFoundException | ElasticsearchException | IllegalArgumentException e) {
            log.error("Handled exception in LogService", e);
            throw e; // Re-throwing for centralized handling
        } catch (Exception e) {
            log.error("Unexpected error while querying Elasticsearch", e);
            throw new RuntimeException("An unexpected error occurred while searching logs.");
        }
    }


    private boolean isIndexPresent(String index) {
        try {
            // Use ElasticsearchClient to check if the index exists
            BooleanResponse response = elasticsearchClient.indices().exists(req -> req.index(index));
            return response.value(); // Returns true if index exists, false otherwise
        } catch (ElasticsearchException e) {
            log.error("Error while checking index existence: {}", index, e);
            return false;
        } catch (Exception e) {
            log.error("Unexpected error while checking index existence: {}", index, e);
            return false;
        }
    }

}
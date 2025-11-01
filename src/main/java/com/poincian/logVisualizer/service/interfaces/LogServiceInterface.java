package com.poincian.logVisualizer.service.interfaces;

import com.poincian.logVisualizer.model.response.LogSearchResponseDTO;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

public interface LogServiceInterface {
    LogSearchResponseDTO searchLogs(String index, String level, String serviceName, String keyword,
                                    LocalDateTime startDate, LocalDateTime endDate,
                                    int page, int size);

    LogSearchResponseDTO searchLogsWithHighlight(String index, String level, String serviceName, String keyword,
                                                 Instant startDate, Instant endDate,
                                                 int page, int size);

    Map<String, Long> getLogCountByLevel(String index) throws IOException;

    Map<String, Long> getLogCountByService(String index) throws IOException;
    Map<String, Long> getLogCountByDate(String index) throws IOException;
}

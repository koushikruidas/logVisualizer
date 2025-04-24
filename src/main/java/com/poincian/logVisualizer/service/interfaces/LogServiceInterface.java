package com.poincian.logVisualizer.service.interfaces;

import com.poincian.logVisualizer.model.LogSearchResponseDTO;

import java.time.LocalDateTime;

public interface LogServiceInterface {
    LogSearchResponseDTO searchLogs(String index, String level, String serviceName, String keyword,
                                    LocalDateTime startDate, LocalDateTime endDate,
                                    int page, int size);
}

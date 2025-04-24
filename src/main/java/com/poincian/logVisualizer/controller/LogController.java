package com.poincian.logVisualizer.controller;

import com.poincian.logVisualizer.model.request.LogHighlightDTO;
import com.poincian.logVisualizer.model.response.LogSearchResponseDTO;
import com.poincian.logVisualizer.service.interfaces.LogServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class LogController {

    private final LogServiceInterface logService;

    @GetMapping
    public ResponseEntity<LogSearchResponseDTO> searchLogs(
            @RequestParam String index,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String serviceName,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(logService.searchLogs(index, level, serviceName, keyword, startDate, endDate, page, size));
    }

    @GetMapping("/search-with-highlight")
    public ResponseEntity<LogSearchResponseDTO> searchLogsWithHighlight(
            @RequestParam String index,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String serviceName,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        LogSearchResponseDTO response = logService.searchLogsWithHighlight(
                index, level, serviceName, keyword, startDate, endDate, page, size);

        return ResponseEntity.ok(response);
    }

}
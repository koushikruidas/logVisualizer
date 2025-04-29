package com.poincian.logVisualizer.controller;

import com.poincian.logVisualizer.service.interfaces.LogServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/logs/aggregation")
@RequiredArgsConstructor
public class AggregationController {

    private final LogServiceInterface logService;

    @GetMapping("/count-by-level")
    public ResponseEntity<Map<String, Long>> getLogCountByLevel(@RequestParam String index) throws IOException {
        return ResponseEntity.ok(logService.getLogCountByLevel(index));
    }

    @GetMapping("/count-by-service")
    public ResponseEntity<Map<String, Long>> getLogCountByService(@RequestParam String index) throws IOException {
        return ResponseEntity.ok(logService.getLogCountByService(index));
    }

    @GetMapping("/count-by-date")
    public ResponseEntity<Map<String, Long>> getLogCountByDate(@RequestParam String index) throws IOException {
        return ResponseEntity.ok(logService.getLogCountByDate(index));
    }
}
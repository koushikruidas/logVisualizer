package com.poincian.logVisualizer.model.response;

import com.poincian.logVisualizer.model.request.LogEntryDocumentDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LogSearchResponseDTO {
    private List<LogEntryDocumentDTO> logs;
    private long totalHits;
}

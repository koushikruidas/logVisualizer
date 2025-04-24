package com.poincian.logVisualizer.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogHighlightDTO {
    private String id;
    private String timestamp;
    private String serviceName;
    private String level;
    private String highlightedMessage; // Highlighted version
    private String originalMessage;    // Original log message
}

package com.poincian.logVisualizer.exception;

public class ElasticsearchException extends RuntimeException {
    public ElasticsearchException(String message) {
        super(message);
    }
}

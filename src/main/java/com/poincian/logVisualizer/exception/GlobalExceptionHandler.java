package com.poincian.logVisualizer.exception;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import com.poincian.logVisualizer.model.response.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IndexNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponseDTO>handleIndexNotFoundException(IndexNotFoundException ex) {
        return new ResponseEntity<>(
                new ErrorResponseDTO(ex.getMessage(), HttpStatus.NOT_FOUND.value()),
                HttpStatus.NOT_FOUND
        );
    }
    @ExceptionHandler(ElasticsearchException.class)
    public ResponseEntity<ErrorResponseDTO> handleElasticsearchException(ElasticsearchException ex) {
        return new ResponseEntity<>(
                new ErrorResponseDTO("Elasticsearch query issue: " + ex.getMessage(),
                        HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(
                new ErrorResponseDTO("Invalid argument: " + ex.getMessage(),
                        HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {
        return new ResponseEntity<>(
                new ErrorResponseDTO("An unexpected error occurred: " + ex.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

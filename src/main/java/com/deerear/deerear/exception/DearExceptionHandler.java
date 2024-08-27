package com.deerear.deerear.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DearExceptionHandler {

    @ExceptionHandler(DearException.class)
    public ResponseEntity<ErrorResponse> handleDearException(DearException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                e.getErrorCodeEnum().getStatus().value(),
                e.getMessage(),
                e.getErrorCodeEnum().getCode(),
                e.getErrorCodeEnum().getMessage(),
                e.getAdditionalMessage()
        );

        return ResponseEntity.status(e.getErrorCodeEnum().getStatus()).body(errorResponse);
    }
}

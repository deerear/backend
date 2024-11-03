package com.deerear.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.deerear.constant.ErrorCode.INVALID_INPUT;

@RestControllerAdvice
public class DataIntegrityViolationExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handle(DataIntegrityViolationException e) {
        ErrorResponse errorResponse = ErrorResponse.of(
                e.getClass().getSimpleName(),
                INVALID_INPUT.getCode(),
                INVALID_INPUT.getMessage(),
                e.getMostSpecificCause().getMessage()

        );
        return ResponseEntity.status(INVALID_INPUT.getStatus()).body(errorResponse);
    }
}

package com.deerear.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.deerear.constant.ErrorCode.INVALID_INPUT;

@RestControllerAdvice
public class ConstraintViolationExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handle(ConstraintViolationException e) {
        ErrorResponse errorResponse = ErrorResponse.of(
                e.getMessage(),
                INVALID_INPUT.getCode(),
                INVALID_INPUT.getMessage(),
                e.getConstraintViolations().iterator().next().getMessage()

        );
        return ResponseEntity.status(INVALID_INPUT.getStatus()).body(errorResponse);
    }
}

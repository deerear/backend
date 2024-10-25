package com.deerear.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.deerear.constant.ErrorCode.INVALID_INPUT;

@RestControllerAdvice
public class MethodArgumentNotValidExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handle(MethodArgumentNotValidException e) {
        ErrorResponse errorResponse = ErrorResponse.of(
                e.getBindingResult().getAllErrors().get(0).getDefaultMessage(),
                INVALID_INPUT.getCode(),
                INVALID_INPUT.getMessage(),
                e.getBindingResult().getAllErrors().get(0).getObjectName()

        );
        return ResponseEntity.status(INVALID_INPUT.getStatus()).body(errorResponse);
    }
}

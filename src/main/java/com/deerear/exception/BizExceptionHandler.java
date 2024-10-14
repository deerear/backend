package com.deerear.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BizExceptionHandler {

    @ExceptionHandler(BizException.class)
    public ResponseEntity<ErrorResponse> handleBizException(BizException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(),
                e.getErrorCodeEnum().getCode(),
                e.getErrorCodeEnum().getMessage(),
                e.getAdditionalMessage()
        );

        return ResponseEntity.status(e.getErrorCodeEnum().getStatus()).body(errorResponse);
    }
}

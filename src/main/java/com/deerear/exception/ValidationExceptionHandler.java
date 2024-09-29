package com.deerear.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.deerear.constant.ErrorCode.INVALID_INPUT;

@RestControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler({ MethodArgumentNotValidException.class, ConstraintViolationException.class })
    public void handleValidationExceptions(Exception exception) {
        if (exception instanceof MethodArgumentNotValidException ex) {
            String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
            throw new BizException(ex.getMessage(), INVALID_INPUT, errorMessage);
        } else if (exception instanceof ConstraintViolationException ex) {
            String errorMessage = ex.getConstraintViolations().iterator().next().getMessage();
            // TODO 아래는 핸들링이 안됨
            throw new BizException(ex.getMessage(), INVALID_INPUT, errorMessage);
        }
    }
}

package com.deerear.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private String errorMessage;
    private String errorCode;
    private String codeMessage;
    private String additionalMessage;

    public static ErrorResponse of(String errorMessage, String errorCode, String codeMessage, String additionalMessage) {
        return new ErrorResponse(errorMessage, errorCode, codeMessage, additionalMessage);
    }
}

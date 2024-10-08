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

}

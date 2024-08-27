package com.deerear.deerear.exception;

import com.deerear.deerear.constant.ErrorCode;
import lombok.Getter;

@Getter
public class DearException extends RuntimeException{

    private final ErrorCode errorCodeEnum;
    private final String additionalMessage;

    public DearException(String errorMessage, ErrorCode errorCodeEnum, String additionalMessage) {
        super(errorMessage);
        this.errorCodeEnum = errorCodeEnum;
        this.additionalMessage = additionalMessage;

    }
}

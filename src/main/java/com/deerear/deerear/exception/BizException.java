package com.deerear.deerear.exception;

import com.deerear.deerear.constant.ErrorCode;
import lombok.Getter;

@Getter
public class BizException extends RuntimeException{

    private final ErrorCode errorCodeEnum;
    private final String additionalMessage;

    public BizException(String errorMessage, ErrorCode errorCodeEnum, String additionalMessage) {
        super(errorMessage);
        this.errorCodeEnum = errorCodeEnum;
        this.additionalMessage = additionalMessage;

    }
}

package com.deerear.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, "10000", "조회 결과가 없습니다."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "10001", "올바르지 않은 입력값 형식입니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}

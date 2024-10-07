package com.deerear.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, "10000", "조회 결과가 없습니다."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "10001", "올바르지 않은 입력값 형식입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "20000", "해당하는 회원을 찾을 수 없습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "20001", "비밀번호가 잘못되었습니다."),
    USERNAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "20002", "이미 사용 중인 사용자 이름입니다."),
    NOT_NULL(HttpStatus.BAD_REQUEST, "20002", "필수 입력값이 없습니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}

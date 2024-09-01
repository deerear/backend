package com.deerear.deerear.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TokenResponseDto {
    private String accessToken;
    private String refreshToken;

    // 기본 생성자
    public TokenResponseDto() {}

    // 모든 필드를 초기화하는 생성자
    public TokenResponseDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
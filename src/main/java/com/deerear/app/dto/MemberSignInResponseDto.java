package com.deerear.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class MemberSignInResponseDto {
    private String grantType;
    private String accessToken;
    private String refreshToken;

    public static MemberSignInResponseDto toDto(String grantType, String accessToken, String refreshToken) {
        return MemberSignInResponseDto.builder()
                .grantType(grantType)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}

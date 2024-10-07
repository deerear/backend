package com.deerear.app.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberCheckNicknameResponseDto {
    private boolean available;

    public static MemberCheckNicknameResponseDto toDto(boolean available) {
        return MemberCheckNicknameResponseDto.builder()
                .available(available)
                .build();
    }
}
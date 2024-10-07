package com.deerear.app.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberCheckEmailResponseDto {

    private boolean available;
    public static MemberCheckEmailResponseDto toDto(boolean available) {
        return MemberCheckEmailResponseDto.builder()
                .available(available)
                .build();
    }
}

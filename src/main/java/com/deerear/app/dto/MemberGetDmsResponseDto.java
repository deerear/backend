package com.deerear.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberGetDmsResponseDto {

    private UUID dmId;
    private String message;
    private Date createdAt;

    public static MemberGetDmsResponseDto toDto(UUID dmId, String message, Date createdAt) {
        return new MemberGetDmsResponseDto(dmId, message, createdAt);
    }
}

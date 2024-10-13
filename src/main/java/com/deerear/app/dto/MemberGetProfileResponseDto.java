package com.deerear.app.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberGetProfileResponseDto {
    private String nickname;
    private String email;
    private String profileImgUrl;

    public static MemberGetProfileResponseDto toDto(String nickname ,String email ,String profileImgUrl){
        return MemberGetProfileResponseDto.builder()
                .nickname(nickname)
                .email(email)
                .profileImgUrl(profileImgUrl)
                .build();
    }
}

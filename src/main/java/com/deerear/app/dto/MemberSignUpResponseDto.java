package com.deerear.app.dto;

import com.deerear.app.domain.Member;
import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberSignUpResponseDto {

    private String email;  // 이메일
    private String password; // 비밀번호 추가
    private String nickname; // 닉네임

    static public MemberSignUpResponseDto toDto(Member member) {
        return MemberSignUpResponseDto.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .build();
    }
}
package com.deerear.app.dto;

import com.deerear.app.domain.Member;
import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDto {

    private String email;  // 이메일
    private String password; // 비밀번호 추가
    private String nickname; // 닉네임

    static public MemberDto toDto(Member member) {
        return MemberDto.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .build();
    }

    public Member toEntity() {
        return Member.builder()
                .email(email)
                .password(password) // 비밀번호 설정
                .nickname(nickname)
                .build();
    }
}
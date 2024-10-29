package com.deerear.app.dto;

import com.deerear.app.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberSignUpRequestDto {

    private String email;
    private String password;
    private String nickname;

    public Member toEntity(String encodedPassword) {

        return Member.builder()
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
               // .address(address)
               // .phone(phone)
               // .profileImg(profileImg)
                //.roles(roles)
                .build();
    }
}
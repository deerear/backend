package com.deerear.app.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class MemberSignInRequestDto {
    private String email;
    private String password;
}

package com.deerear.app.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class SignInDto {
    private String username;
    private String password;
}

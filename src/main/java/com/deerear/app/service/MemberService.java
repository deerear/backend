package com.deerear.app.service;

import com.deerear.app.dto.JwtToken;
import com.deerear.app.dto.MemberDto;
import com.deerear.app.dto.SignInDto;
import com.deerear.app.dto.SignUpDto;

public interface MemberService {
    JwtToken signIn(String username, String password);

    MemberDto signUp(SignUpDto signUpDto);

    JwtToken signIn(SignInDto signInDto);
}

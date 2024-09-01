package com.deerear.deerear.service;

import com.deerear.deerear.dto.JwtToken;
import com.deerear.deerear.dto.MemberDto;
import com.deerear.deerear.dto.SignInDto;
import com.deerear.deerear.dto.SignUpDto;

public interface MemberService {
    JwtToken signIn(String username, String password);

    MemberDto signUp(SignUpDto signUpDto);

    JwtToken signIn(SignInDto signInDto);
}

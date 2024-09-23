package com.deerear.app.service;

import com.deerear.app.domain.Member;
import com.deerear.app.repository.MemberRepository;
import com.deerear.constant.ErrorCode;
import com.deerear.exception.BizException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        return memberRepository.findByEmail(email)
                .map(this::createUserDetails)
                .orElseThrow(() -> new BizException("해당하는 회원을 찾을 수 없습니다.", ErrorCode.USER_NOT_FOUND, "email: " + email));
    }

    private UserDetails createUserDetails(Member member) {
        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .authorities(new ArrayList<>())  // 빈 권한 목록을 설정
                .build();
    }
}
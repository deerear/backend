package com.deerear.app.service;

import com.deerear.app.domain.Member;
import com.deerear.app.repository.MemberRepository;
import com.deerear.constant.ErrorCode;
import com.deerear.exception.BizException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String email) {
        return memberRepository.findByEmail(email)
                .map(this::createCustomUserDetails)
                .orElseThrow(() -> new BizException("해당하는 회원을 찾을 수 없습니다.", ErrorCode.USER_NOT_FOUND, "email: " + email));
    }

    private CustomUserDetails createCustomUserDetails(Member member) {
        return CustomUserDetails.of(member);
    }
}
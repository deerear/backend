package com.deerear.app.service;

import com.deerear.app.domain.Member;
import com.deerear.app.dto.JwtToken;
import com.deerear.app.dto.MemberDto;
import com.deerear.app.dto.SignInDto;
import com.deerear.app.dto.SignUpDto;
import com.deerear.constant.ErrorCode;
import com.deerear.exception.BizException;
import com.deerear.jwt.JwtTokenProvider;
import com.deerear.app.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public JwtToken signIn(String username, String password) {
        // 1. 검증 단계
        validateSignIn(username, password);

        // 2. 로직 단계
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new BizException("해당하는 회원을 찾을 수 없습니다.", ErrorCode.USER_NOT_FOUND, "username: " + username));

        boolean matches = passwordEncoder.matches(password, member.getPassword());
        if (!matches) {
            throw new BizException("비밀번호가 잘못되었습니다.", ErrorCode.INVALID_PASSWORD, "username: " + username);
        }

        Authentication authentication = authenticateUser(username, password);
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        // 3. return
        saveRefreshToken(member, jwtToken);
        return jwtToken;
    }

    private void validateSignIn(String username, String password) {
        if (username == null || password == null) {
            throw new BizException("사용자 이름 또는 비밀번호가 누락되었습니다.", ErrorCode.INVALID_INPUT, "username: " + username);
        }
    }

    private Authentication authenticateUser(String username, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    }

    private void saveRefreshToken(Member member, JwtToken jwtToken) {
        String refreshToken = jwtToken.getRefreshToken();
        member.setRefreshToken(refreshToken);
        memberRepository.save(member); // 데이터베이스에 리프레시 토큰 저장
    }

    @Transactional
    @Override
    public MemberDto signUp(SignUpDto signUpDto) {
        // 1. 검증 단계
        validateSignUp(signUpDto);

        // 2. 로직 단계
        String encodedPassword = passwordEncoder.encode(signUpDto.getPassword());
        List<String> roles = new ArrayList<>();
        roles.add("USER");

        Member savedMember = memberRepository.save(signUpDto.toEntity(encodedPassword, roles));

        // 3. return
        return MemberDto.toDto(savedMember);
    }

    private void validateSignUp(SignUpDto signUpDto) {
        if (memberRepository.existsByUsername(signUpDto.getUsername())) {
            throw new BizException("이미 사용 중인 사용자 이름입니다.", ErrorCode.INVALID_INPUT, "username: " + signUpDto.getUsername());
        }
    }

    @Override
    public JwtToken signIn(SignInDto signInDto) {
        // 1. 검증 단계
        validateSignIn(signInDto.getUsername(), signInDto.getPassword());

        // 2. 로직 단계
        Member member = memberRepository.findByUsername(signInDto.getUsername())
                .orElseThrow(() -> new BizException("해당하는 회원을 찾을 수 없습니다.", ErrorCode.USER_NOT_FOUND, "username: " + signInDto.getUsername()));

        boolean matches = passwordEncoder.matches(signInDto.getPassword(), member.getPassword());
        if (!matches) {
            throw new BizException("비밀번호가 잘못되었습니다.", ErrorCode.INVALID_PASSWORD, "username: " + signInDto.getUsername());
        }

        Authentication authentication = authenticateUser(signInDto.getUsername(), signInDto.getPassword());
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        // 3. return
        return jwtToken;
    }

    @Transactional
    public void changePassword(String username, String newPassword) {
        // 1. 검증 단계
        validateChangePassword(username, newPassword);

        // 2. 로직 단계
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new BizException("해당하는 회원을 찾을 수 없습니다.", ErrorCode.USER_NOT_FOUND, "username: " + username));

        String encodedPassword = passwordEncoder.encode(newPassword);
        member.setPassword(encodedPassword);
        memberRepository.save(member);
    }

    private void validateChangePassword(String username, String newPassword) {
        if (username == null || newPassword == null) {
            throw new BizException("사용자 이름 또는 새 비밀번호가 누락되었습니다.", ErrorCode.INVALID_INPUT, "username: " + username);
        }
    }
}
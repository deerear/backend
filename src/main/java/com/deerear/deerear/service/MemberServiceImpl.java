package com.deerear.deerear.service;

import com.deerear.deerear.domain.Member;
import com.deerear.deerear.dto.JwtToken;
import com.deerear.deerear.dto.MemberDto;
import com.deerear.deerear.dto.SignInDto;
import com.deerear.deerear.dto.SignUpDto;
import com.deerear.deerear.jwt.JwtTokenProvider;
import com.deerear.deerear.repository.MemberRepository;
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
        log.info("Attempting to sign in user with username: {}", username);

        // 1. 사용자 정보 조회
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다.");
                });

        // 2. 비밀번호 검증
        boolean matches = passwordEncoder.matches(password, member.getPassword());
        if (!matches) {
            log.warn("Password mismatch for user: {}", username);
            throw new BadCredentialsException("비밀번호가 잘못되었습니다.");
        }
        log.info("Password match for user: {}", username);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        // 4. 리프레시 토큰 저장
        String refreshToken = jwtToken.getRefreshToken();
        member.setRefreshToken(refreshToken);
        memberRepository.save(member); // 데이터베이스에 리프레시 토큰 저장

        log.info("User signed in successfully: {}", username);
        log.debug("Generated JWT Token: {}", jwtToken);

        return jwtToken;
    }

    @Transactional
    @Override
    public MemberDto signUp(SignUpDto signUpDto) {
        log.info("Attempting to sign up user with username: {}", signUpDto.getUsername());

        if (memberRepository.existsByUsername(signUpDto.getUsername())) {
            log.error("Username already exists: {}", signUpDto.getUsername());
            throw new IllegalArgumentException("이미 사용 중인 사용자 이름입니다.");
        }

        // Password 암호화
        String encodedPassword = passwordEncoder.encode(signUpDto.getPassword());
        List<String> roles = new ArrayList<>();
        roles.add("USER");  // USER 권한 부여

        Member savedMember = memberRepository.save(signUpDto.toEntity(encodedPassword, roles));
        log.info("User signed up successfully: {}", savedMember.getUsername());

        return MemberDto.toDto(savedMember);
    }

    @Override
    public JwtToken signIn(SignInDto signInDto) {
        log.info("Attempting to sign in user with username: {}", signInDto.getUsername());

        // 1. 사용자 정보 조회
        Member member = memberRepository.findByUsername(signInDto.getUsername())
                .orElseThrow(() -> {
                    log.error("User not found: {}", signInDto.getUsername());
                    return new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다.");
                });

        // 2. 비밀번호 검증
        boolean matches = passwordEncoder.matches(signInDto.getPassword(), member.getPassword());
        if (!matches) {
            log.warn("Password mismatch for user: {}", signInDto.getUsername());
            throw new BadCredentialsException("비밀번호가 잘못되었습니다.");
        }
        log.info("Password match for user: {}", signInDto.getUsername());

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(signInDto.getUsername(), signInDto.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        log.info("User signed in successfully: {}", signInDto.getUsername());
        log.debug("Generated JWT Token: {}", jwtToken);

        return jwtToken;
    }

    @Transactional
    public void changePassword(String username, String newPassword) {
        log.info("Attempting to change password for user: {}", username);

        // 1. 사용자 조회
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다.");
                });

        // 2. 비밀번호 인코딩
        String encodedPassword = passwordEncoder.encode(newPassword);

        // 3. 인코딩된 비밀번호로 업데이트
        member.setPassword(encodedPassword);
        memberRepository.save(member);

        log.info("Password updated successfully for user: {}", username);
    }
}
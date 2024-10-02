package com.deerear.app.service;

import com.deerear.app.domain.Member;
import com.deerear.app.dto.JwtToken;
import com.deerear.app.dto.MemberDto;
import com.deerear.app.dto.SignInDto;
import com.deerear.app.dto.SignUpDto;
import com.deerear.app.repository.MemberRepository;
import com.deerear.constant.ErrorCode;
import com.deerear.exception.BizException;
import com.deerear.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public JwtToken signIn(String email, String password) {
        // 1. 검증 단계
        validateSignIn(email, password);

        // 2. 로직 단계
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BizException("해당하는 회원을 찾을 수 없습니다.", ErrorCode.USER_NOT_FOUND, "email: " + email));

        boolean matches = passwordEncoder.matches(password, member.getPassword());
        if (!matches) {
            throw new BizException("비밀번호가 잘못되었습니다.", ErrorCode.INVALID_PASSWORD, "email: " + email);
        }

        Authentication authentication = authenticateUser(email, password);
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        // 3. return
        saveRefreshToken(member, jwtToken);
        return jwtToken;
    }

    private void validateSignIn(String email, String password) {
        if (email == null || password == null) {
            throw new BizException("이메일이나 비밀번호를 입력해주세요.", ErrorCode.INVALID_INPUT, "email: " + email);
        }
    }

    private Authentication authenticateUser(String email, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        return authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    }

    private void saveRefreshToken(Member member, JwtToken jwtToken) {
        String refreshToken = jwtToken.getRefreshToken();
        member.setRefreshToken(refreshToken);
        memberRepository.save(member); // 데이터베이스에 리프레시 토큰 저장
    }

    @Transactional
    public MemberDto signUp(SignUpDto signUpDto) {
        // 1. 검증 단계
        validateSignUp(signUpDto);

        // 2. 로직 단계
        String encodedPassword = passwordEncoder.encode(signUpDto.getPassword());
        //List<String> roles = new ArrayList<>();
        //roles.add("USER");

        Member savedMember = memberRepository.save(signUpDto.toEntity(encodedPassword));

        // 3. return
        return MemberDto.toDto(savedMember);
    }

    private void validateSignUp(SignUpDto signUpDto) {
        if (memberRepository.existsByEmail(signUpDto.getEmail())) {
            throw new BizException("이미 사용 중인 사용자 이메일입니다.", ErrorCode.USERNAME_ALREADY_EXISTS, "email: " + signUpDto.getEmail());
        }
    }

    @Transactional
    public JwtToken signIn(SignInDto signInDto) {
        // 1. 검증 단계
        validateSignIn(signInDto.getEmail(), signInDto.getPassword());

        // 2. 로직 단계
        Member member = memberRepository.findByEmail(signInDto.getEmail())
                .orElseThrow(() -> new BizException("해당하는 회원을 찾을 수 없습니다.", ErrorCode.USER_NOT_FOUND, "email: " + signInDto.getEmail()));

        boolean matches = passwordEncoder.matches(signInDto.getPassword(), member.getPassword());
        if (!matches) {
            throw new BizException("비밀번호가 잘못되었습니다.", ErrorCode.INVALID_PASSWORD, "password: " + "");
        }

        Authentication authentication = authenticateUser(signInDto.getEmail(), signInDto.getPassword());
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        // 3. return
        return jwtToken;
    }

    @Transactional
    public void changePassword(String email, String newPassword) {
        // 1. 검증 단계
        validateChangePassword(email, newPassword);

        // 2. 로직 단계
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BizException("해당하는 회원을 찾을 수 없습니다.", ErrorCode.USER_NOT_FOUND, "email: " + email));

        String encodedPassword = passwordEncoder.encode(newPassword);
        member.setPassword(encodedPassword);
        memberRepository.save(member);
    }

    private void validateChangePassword(String email, String newPassword) {
        if (email == null || newPassword == null) {
            throw new BizException("사용자 이름 또는 새 비밀번호가 누락되었습니다.", ErrorCode.INVALID_INPUT, "email: " + email);
        }
    }
}
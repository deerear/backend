package com.deerear.app.service;

import com.deerear.app.domain.Member;
import com.deerear.app.dto.*;
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


    @Transactional
    public MemberSignInResponseDto signIn(MemberSignInRequestDto memberSignInRequestDto) {
        validateSignIn(memberSignInRequestDto.getEmail(), memberSignInRequestDto.getPassword());

        Member member = memberRepository.findByEmail(memberSignInRequestDto.getEmail())
                .orElseThrow(() -> new BizException("해당하는 회원을 찾을 수 없습니다.", ErrorCode.USER_NOT_FOUND, "email: " + memberSignInRequestDto.getEmail()));

        boolean matches = passwordEncoder.matches(memberSignInRequestDto.getPassword(), member.getPassword());
        if (!matches) {
            throw new BizException("비밀번호가 잘못되었습니다.", ErrorCode.INVALID_PASSWORD, "password: " + "");
        }

        Authentication authentication = authenticateUser(memberSignInRequestDto.getEmail(), memberSignInRequestDto.getPassword());
        MemberSignInResponseDto memberSignInResponseDto = jwtTokenProvider.generateToken(authentication);

        // 리프레시 토큰 저장
        saveRefreshToken(member, memberSignInResponseDto);

        return MemberSignInResponseDto.toDto(memberSignInResponseDto.getAccessToken(), memberSignInResponseDto.getRefreshToken());
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

    private void saveRefreshToken(Member member, MemberSignInResponseDto memberSignInResponseDto) {
        String refreshToken = memberSignInResponseDto.getRefreshToken();
        member.setRefreshToken(refreshToken);
        memberRepository.save(member); // 데이터베이스에 리프레시 토큰 저장
    }

    @Transactional
    public MemberSignUpResponseDto signUp(MemberSingUpRequestDto memberSingUpRequestDto) {
        // 1. 검증 단계
        validateSignUp(memberSingUpRequestDto);

        // 2. 로직 단계
        String encodedPassword = passwordEncoder.encode(memberSingUpRequestDto.getPassword());
        //List<String> roles = new ArrayList<>();
        //roles.add("USER");

        Member savedMember = memberRepository.save(memberSingUpRequestDto.toEntity(encodedPassword));

        // 3. return
        return MemberSignUpResponseDto.toDto(savedMember);
    }

    private void validateSignUp(MemberSingUpRequestDto requestDto) {
        if (memberRepository.existsByEmail(requestDto.getEmail())) {
            throw new BizException("이미 사용 중인 사용자 이메일입니다.", ErrorCode.USERNAME_ALREADY_EXISTS, "email: " + requestDto.getEmail());
        }
    }

    @Transactional(readOnly = true)
    public MemberCheckNicknameResponseDto checkNickname(MemberCheckNicknameRequestDto memberCheckNicknameRequestDto) {
        validateCheckNickname(memberCheckNicknameRequestDto);
        return MemberCheckNicknameResponseDto.toDto(true);  // 사용 가능한 닉네임이면 true 반환
    }

    private void validateCheckNickname(MemberCheckNicknameRequestDto memberCheckNicknameRequestDto) {
        //TODO 에러코드 추후 협의해봐야할듯
        if (memberRepository.existsByNickname(memberCheckNicknameRequestDto.getNickname())) {
            throw new BizException("이미 사용 중인 닉네임입니다.", ErrorCode.INVALID_INPUT, "닉네임: " + memberCheckNicknameRequestDto.getNickname());
        }
    }

    @Transactional(readOnly = true)
    public MemberCheckEmailResponseDto checkEmail(MemberCheckEmailRequestDto requestDto) {
        validateCheckEmail(requestDto);
        return MemberCheckEmailResponseDto.toDto(true);  // 사용 가능한 닉네임이면 true 반환
    }

    private void validateCheckEmail(MemberCheckEmailRequestDto memberCheckEmailRequestDto) {
        if (memberRepository.existsByEmail(memberCheckEmailRequestDto.getEmail())) {
            throw new BizException("이미 사용 중인 사용자 이메일입니다.", ErrorCode.USERNAME_ALREADY_EXISTS, "email: " + memberCheckEmailRequestDto.getEmail());
        }
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
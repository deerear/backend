package com.deerear.app.controller;

import com.deerear.app.domain.Member;
import com.deerear.app.dto.TokenRequestDto;
import com.deerear.app.dto.TokenResponseDto;
import com.deerear.app.repository.MemberRepository;
import com.deerear.constant.ErrorCode;
import com.deerear.exception.BizException;
import com.deerear.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/token")
@RequiredArgsConstructor
@Slf4j
public class TokenController {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refreshAccessToken(@RequestBody TokenRequestDto tokenRequestDto) {
        String refreshToken = tokenRequestDto.getRefreshToken();

        // 리프레시 토큰의 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            log.error("Invalid refresh token: {}", refreshToken);
            throw new BizException("리프레시 토큰이 유효하지 않습니다.", ErrorCode.INVALID_INPUT, "refreshToken: " + refreshToken);
        }

        // 리프레시 토큰에서 사용자 정보 추출
        String username;
        try {
            username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        } catch (RuntimeException e) {
            log.error("Error extracting username from refresh token", e);
            throw new BizException("리프레시 토큰에서 사용자 정보를 추출할 수 없습니다.", ErrorCode.INVALID_INPUT, "refreshToken: " + refreshToken);
        }

        // DB에 저장된 리프레시 토큰과 요청된 리프레시 토큰의 일치 여부 확인
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new BizException("해당하는 회원을 찾을 수 없습니다.", ErrorCode.USER_NOT_FOUND, "username: " + username));

        if (!refreshToken.equals(member.getRefreshToken())) {
            throw new BizException("리프레시 토큰이 일치하지 않습니다.", ErrorCode.INVALID_INPUT, "refreshToken: " + refreshToken);
        }

        long now = System.currentTimeMillis();
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());

        // 새로운 액세스 토큰 및 리프레시 토큰 생성
        String newAccessToken = jwtTokenProvider.generateAccessToken(authentication, now);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(username, now);

        // 리프레시 토큰을 새로 발급받아 DB에 저장
        member.setRefreshToken(newRefreshToken);
        memberRepository.save(member);

        TokenResponseDto response = TokenResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();

        return ResponseEntity.ok(response);
    }
}
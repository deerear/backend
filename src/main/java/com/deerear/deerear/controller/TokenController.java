package com.deerear.deerear.controller;

import com.deerear.deerear.domain.Member;
import com.deerear.deerear.dto.TokenRequestDto;
import com.deerear.deerear.dto.TokenResponseDto;
import com.deerear.deerear.jwt.JwtTokenProvider;
import com.deerear.deerear.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new TokenResponseDto("리프레시 토큰이 유효하지 않습니다.", null));
        }

        // 리프레시 토큰에서 사용자 정보 추출 (권한 없이 사용자 정보만 추출)
        String username;
        try {
            username = jwtTokenProvider.getUsernameFromToken(refreshToken);  // 사용자 이름만 추출하는 메서드
        } catch (RuntimeException e) {
            log.error("Error extracting username from refresh token", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new TokenResponseDto("리프레시 토큰에서 사용자 정보를 추출할 수 없습니다.", null));
        }

        log.debug("Username from refresh token: {}", username);

        // DB에 저장된 리프레시 토큰과 요청된 리프레시 토큰의 일치 여부 확인 (교차 검증)
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다.");
                });

        if (!refreshToken.equals(member.getRefreshToken())) {
            log.error("Refresh token mismatch for user: {}", username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new TokenResponseDto("리프레시 토큰이 일치하지 않습니다.", null));
        }

        long now = System.currentTimeMillis();
        // Username을 기반으로 Authentication 객체 생성 (권한 정보를 설정)
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());

        // 새로운 액세스 토큰 생성
        String newAccessToken = jwtTokenProvider.generateAccessToken(authentication, now);

        // 새로운 리프레시 토큰 생성
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(username, now);

        // 리프레시 토큰을 새로 발급받아 DB에 저장
        member.setRefreshToken(newRefreshToken);
        memberRepository.save(member);

        TokenResponseDto response = TokenResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken) // 새로 발급된 리프레시 토큰
                .build();

        return ResponseEntity.ok(response);
    }
}
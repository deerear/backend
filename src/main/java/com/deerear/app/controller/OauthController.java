package com.deerear.app.controller;

import com.deerear.app.dto.KakaoDto;
import com.deerear.app.dto.MemberSignInResponseDto;
import com.deerear.app.dto.NaverDto;
import com.deerear.app.service.KakaoService;
import com.deerear.app.service.NaverService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth")
public class OauthController {

    private final NaverService naverService;
    private final KakaoService kakaoService;

    @GetMapping("/naver")
    public ResponseEntity<String> loginWithNaver() {
        String naverLoginUrl = naverService.getNaverLogin();
        return ResponseEntity.ok(naverLoginUrl);
    }

    @GetMapping("/naver/callback")
    public ResponseEntity<MemberSignInResponseDto> callbackWithNaver(HttpServletRequest request) throws Exception {
        // 네이버에서 전달된 코드를 사용하여 사용자 정보를 가져옴
        NaverDto naverInfo = naverService.getNaverInfo(request.getParameter("code"));

        // 네이버 정보를 사용하여 회원가입 및 로그인 처리
        MemberSignInResponseDto responseDto = naverService.signIn(naverInfo);

        return ResponseEntity.ok(responseDto);
    }

    @Value("${KAKAO_CLIENT_ID}")
    private String clientId;
    @Value("${KAKAO_REDIRECT_URI}")
    private String redirectUri;

    @GetMapping("/kakao")
    public ResponseEntity<String> loginWithKokao() {
        String kakaoLoginUrl = kakaoService.getKakaoLogin();
        return ResponseEntity.ok(kakaoLoginUrl);
    }

    @GetMapping("/kakao/callback")
    public ResponseEntity<MemberSignInResponseDto> callbackWithKakao(HttpServletRequest request) throws Exception {
        KakaoDto kakaoInfo = kakaoService.getKakaoInfo(request.getParameter("code"));

        MemberSignInResponseDto responseDto = kakaoService.signIn(kakaoInfo);

        return ResponseEntity.ok(responseDto);
    }

}

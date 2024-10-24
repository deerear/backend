//package com.deerear.app.controller;
//
//import com.deerear.app.service.KakaoService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.IOException;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/members/login/kakao")
//public class KakaoLoginController {
//
//
//    private final KakaoService kakaoService;
//
//    @Value("${KAKAO_CLIENT_ID}")
//    private String clientId;
//    @Value("${KAKAO_REDIRECT_URI}")
//    private String redirectUri;
//
//    @GetMapping("")
//    public ResponseEntity<String> getKakaoAuthUrl() {
//        String location = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=" + clientId + "&redirect_uri=" + redirectUri;
//        return ResponseEntity.ok(location);
//    }
//
//    @GetMapping("/callback")
//    public ResponseEntity<?> callback(@RequestParam("code") String code) throws IOException {
//        String accessToken = kakaoService.getAccessTokenFromKakao(code);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//}

//package com.deerear.app.controller;
//
//import com.deerear.app.domain.Member;
//import com.deerear.app.dto.MemberSignInRequestDto;
//import com.deerear.app.dto.MemberSignInResponseDto;
//import com.deerear.app.dto.MemberSingUpRequestDto;
//import com.deerear.app.dto.NaverDto;
//import com.deerear.app.repository.MemberRepository;
//import com.deerear.app.service.MemberService;
//import com.deerear.app.service.NaverService;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Optional;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/members/login/naver")
//public class NaverLoginController {
//    private final NaverService naverService;
//    private final MemberRepository memberRepository;
//    private final MemberService memberService;
//
//    @GetMapping("")
//    public ResponseEntity<String> loginWithNaver() {
//        String naverLoginUrl = naverService.getNaverLogin();
//        return ResponseEntity.ok(naverLoginUrl);
//    }
//
//    @GetMapping("/callback")
//    public ResponseEntity<?> callback(HttpServletRequest request) throws Exception {
//        NaverDto naverInfo = naverService.getNaverInfo(request.getParameter("code"));
//        String email = naverInfo.getEmail();
//        Optional<Member> optionalMember = memberRepository.findByEmail(email);
//
//        if (optionalMember.isEmpty()) {
//            // 새로운 회원 가입
//            MemberSingUpRequestDto signUpRequestDto = MemberSingUpRequestDto.builder()
//                    .email(email)
//                    .nickname(naverInfo.getNickname())
//                    .password(null) // 비밀번호는 null
//                    .build();
//            memberService.signUp(signUpRequestDto);
//
//            return ResponseEntity.ok(MemberSignInResponseDto.toDto("회원가입 성공", null));
//        } else {
//            // 기존 회원의 경우 로그인 처리
//            MemberSignInRequestDto signInRequestDto = MemberSignInRequestDto.builder()
//                    .email(email)
//                    .password(null) // 비밀번호는 필요 없으므로 null
//                    .build();
//
//            naverService.signIn()
//
//            return ResponseEntity.ok(MemberSignInResponseDto.toDto("로그인 성공", )); // JWT 토큰 반환
//        }
//    }
//}
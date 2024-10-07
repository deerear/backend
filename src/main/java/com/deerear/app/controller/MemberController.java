package com.deerear.app.controller;

import com.deerear.app.dto.*;
import com.deerear.app.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/sign-in")
    public ResponseEntity<MemberSignInResponseDto> signIn(@RequestBody MemberSignInRequestDto requestDto) {
        MemberSignInResponseDto responseDto = memberService.signIn(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<MemberSignUpResponseDto> signUp(@RequestBody MemberSingUpRequestDto memberSingUpRequestDto) {
        MemberSignUpResponseDto savedMemberSignUpResponseDto = memberService.signUp(memberSingUpRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMemberSignUpResponseDto);
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<MemberCheckNicknameResponseDto> checkNickname(@RequestParam String nickname) {
        MemberCheckNicknameRequestDto requestDto = new MemberCheckNicknameRequestDto(nickname);
        MemberCheckNicknameResponseDto responseDto = memberService.checkNickname(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/check-email")
    public ResponseEntity<MemberCheckEmailResponseDto> checkEmail(@RequestParam String email) {
        MemberCheckEmailRequestDto requestDto = new MemberCheckEmailRequestDto(email);
        MemberCheckEmailResponseDto responseDto = memberService.checkEmail(requestDto);
        return ResponseEntity.ok(responseDto);
    }
}
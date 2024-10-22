package com.deerear.app.controller;

import com.deerear.app.domain.Member;
import com.deerear.app.dto.*;
import com.deerear.app.repository.MemberRepository;
import com.deerear.app.service.CommentService;
import com.deerear.app.service.CustomUserDetails;
import com.deerear.app.service.MemberService;
import com.deerear.app.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;
    private final PostService postService;
    private final CommentService commentService;
    private final MemberRepository memberRepository;

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

    @GetMapping("/posts")
    public ResponseEntity<PagingResponseDto> getPosts(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                @ModelAttribute PagingRequestDto page) {
        Member member = customUserDetails.getUser();
        PagingResponseDto responseDto =  postService.getPosts(member, page);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/comments")
    public ResponseEntity<PagingResponseDto> getComments(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                      @ModelAttribute PagingRequestDto page) {
        Member member = customUserDetails.getUser();
        PagingResponseDto responseDto =  commentService.getComments(member, page);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/profile")
    public ResponseEntity<MemberGetProfileResponseDto> getProfile(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Member member = customUserDetails.getUser();
        MemberGetProfileResponseDto responseDto = memberService.getProfile(member);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> updateProfile(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @ModelAttribute MemberUpdateRequestDto request) {

        // 프로필 업데이트 로직
        memberService.updateProfile(customUserDetails, request);

        return ResponseEntity.status(HttpStatus.OK).body("프로필이 성공적으로 업데이트되었습니다.");
    }

}
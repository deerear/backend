package com.deerear.app.controller;

import com.deerear.app.dto.LikeSaveRequestDto;
import com.deerear.app.service.CustomUserDetails;
import com.deerear.app.service.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequestMapping("/api/likes")
@RequiredArgsConstructor
@RestController
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/post")
    public ResponseEntity<Void> toggleLikePost(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                               @Valid @RequestBody LikeSaveRequestDto input) {
        likeService.likePost(customUserDetails.getUser(), UUID.fromString(input.getId()));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/comment")
    public ResponseEntity<Void> toggleLikeComment(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                  @Valid @RequestBody LikeSaveRequestDto input) {
        likeService.likeComment(customUserDetails.getUser(), UUID.fromString(input.getId()));
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}


package com.deerear.app.controller;

import com.deerear.app.dto.CommentsCreateRequestDto;
import com.deerear.app.dto.CommentsCreateResponseDto;
import com.deerear.app.service.CommentsService;
import com.deerear.app.service.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/commnets")
@RequiredArgsConstructor
@RestController
public class CommentsController {

    private final CommentsService commentsService;

    @PostMapping
    public ResponseEntity<CommentsCreateResponseDto> createComment(@AuthenticationPrincipal CustomUserDetails user, @Valid @RequestBody CommentsCreateRequestDto input) {

        System.out.println(user);
        CommentsCreateResponseDto output = commentsService.createComment(user, input);

        return ResponseEntity.status(HttpStatus.CREATED).body(output);
    }
}


package com.deerear.app.controller;

import com.deerear.app.dto.CommentsCreateRequestDto;
import com.deerear.app.dto.CommentsCreateResponseDto;
import com.deerear.app.service.CommentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/commnets")
@RequiredArgsConstructor
@RestController
public class CommentsController {

    private final CommentsService commentsService;

    @PostMapping
    public ResponseEntity<CommentsCreateResponseDto> createComment(@AuthenticationPrincipal Object user, @RequestBody CommentsCreateRequestDto input) {

        CommentsCreateResponseDto output = commentsService.createComment(user, input);

        return ResponseEntity.status(HttpStatus.CREATED).body(output);
    }
}


package com.deerear.app.controller;

import com.deerear.app.dto.CommentSaveRequestDto;
import com.deerear.app.dto.PagingRequestDto;
import com.deerear.app.dto.PagingResponseDto;
import com.deerear.app.service.CommentService;
import com.deerear.app.service.CustomUserDetails;
import com.deerear.exception.BizException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.deerear.constant.ErrorCode.NOT_NULL;

@RequestMapping("/api/comments")
@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Void> createComment(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                              @Valid @RequestBody CommentSaveRequestDto input, Errors errors) {
        // TODO 이 로직 제거 할 예정~~~
        if (errors.hasErrors()) {
            throw new BizException(null, NOT_NULL, errors.getAllErrors().get(0).getDefaultMessage());
        }
        commentService.createComment(customUserDetails.getUser(), input);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Void> updateComment(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                              @PathVariable UUID commentId,
                                              @Valid @RequestBody CommentSaveRequestDto input, Errors errors) {
        if (errors.hasErrors()) {
            throw new BizException(null, NOT_NULL, errors.getAllErrors().get(0).getDefaultMessage());
        }
        commentService.updateComment(customUserDetails.getUser(), input, commentId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                              @PathVariable UUID commentId) {
        commentService.deleteComment(customUserDetails.getUser(), commentId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("{postId}")
    public ResponseEntity<PagingResponseDto> listComment(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                @PathVariable UUID postId,
                                                                @RequestBody PagingRequestDto page) {
        return ResponseEntity.ok(commentService.listComment(customUserDetails.getUser(), postId, page));
    }
}


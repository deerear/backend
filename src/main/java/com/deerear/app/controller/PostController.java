package com.deerear.app.controller;

import com.deerear.app.dto.*;
import com.deerear.app.service.CustomUserDetails;
import com.deerear.app.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponseDto> getPost(@PathVariable UUID postId) {
        return ResponseEntity.ok(postService.getPost(postId));
    }

    @GetMapping("")
    public ResponseEntity<PostListResponseDto> listPosts(@AuthenticationPrincipal CustomUserDetails customUserDetails, PostListRequestDto request, @RequestParam String nextKey, @RequestParam Integer size) {
        return ResponseEntity.ok(postService.listPosts(customUserDetails, request, nextKey, size));
    }

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> createPost(@AuthenticationPrincipal CustomUserDetails customUserDetails, @ModelAttribute PostCreateRequestDto request){
        postService.createPost(customUserDetails, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PutMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> updatePost(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID postId, @ModelAttribute PostUpdateRequestDto request){
        postService.updatePost(customUserDetails, postId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Object> deletePost(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID postId){
        postService.deletePost(customUserDetails, postId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<Object> deleteImage(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID imageId){
        postService.deleteImage(customUserDetails, imageId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}

package com.deerear.app.controller;

import com.deerear.app.dto.PostListResponseDto;
import com.deerear.app.dto.PostRequestDto;
import com.deerear.app.dto.PostResponseDto;
import com.deerear.app.service.CustomUserDetails;
import com.deerear.app.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @GetMapping("")
    public ResponseEntity<PostListResponseDto> listPosts(
            @RequestParam String nextKey,
            @RequestParam Integer size,
            @RequestParam BigDecimal startLatitude,
            @RequestParam BigDecimal startLongitude,
            @RequestParam BigDecimal endLatitude,
            @RequestParam BigDecimal endLongitude) {
        return ResponseEntity.ok(postService.listPosts(nextKey, size, startLatitude, startLongitude, endLatitude, endLongitude));
    }
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> getPost(@PathVariable UUID postId) {
        return ResponseEntity.ok(postService.getPost(postId));
    }

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> createPost(@AuthenticationPrincipal CustomUserDetails customUserDetails, @ModelAttribute PostRequestDto request){
        postService.createPost(customUserDetails, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PutMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> updatePost(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID postId, @ModelAttribute PostRequestDto request){
        postService.updatePost(customUserDetails, postId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Object> deletePost(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID postId){
        postService.deletePost(customUserDetails, postId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}

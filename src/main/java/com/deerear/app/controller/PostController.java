package com.deerear.app.controller;

import com.deerear.app.domain.Post;
import com.deerear.app.dto.PostRequestDto;
import com.deerear.app.dto.PostResponseDto;
import com.deerear.app.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> getPost(@PathVariable UUID postId) {
        return ResponseEntity.ok(postService.getPost(postId));
    }

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponseDto> createPost(@RequestHeader(value = "Authorization") String auth, @ModelAttribute PostRequestDto request) throws IOException {
        PostResponseDto postDto = postService.createPost(auth, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(postDto);
    }

    @PutMapping("/{postId}")
    public void updatePost(@PathVariable UUID postId, @RequestBody PostRequestDto postRequestDto){
        return;
    }

    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable UUID postId){
        return;
    }
}

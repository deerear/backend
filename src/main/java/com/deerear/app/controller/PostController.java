package com.deerear.app.controller;

import com.deerear.app.domain.Post;
import com.deerear.app.dto.ListDto;
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
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @GetMapping("")
    public ResponseEntity<ListDto> listPosts(
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
    public ResponseEntity<Object> createPost(@RequestHeader(value = "Authorization") String auth, @ModelAttribute PostRequestDto request) throws IOException {
        postService.createPost(auth, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PutMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> updatePost(@RequestHeader(value = "Authorization") String auth, @PathVariable UUID postId, @ModelAttribute PostRequestDto request){
        postService.updatePost(auth, postId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Object> deletePost(@RequestHeader(value = "Authorization") String auth, @PathVariable UUID postId){
        postService.deletePost(auth, postId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}

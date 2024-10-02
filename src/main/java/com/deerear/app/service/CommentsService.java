package com.deerear.app.service;

import com.deerear.app.domain.Comment;
import com.deerear.app.domain.Member;
import com.deerear.app.domain.Post;
import com.deerear.app.dto.CommentsCreateRequestDto;
import com.deerear.app.dto.CommentsCreateResponseDto;
import com.deerear.app.repository.CommentRepository;
import com.deerear.app.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentsService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentsCreateResponseDto createComment(CustomUserDetails userDetails, CommentsCreateRequestDto input) {

        Member memberEntity = userDetails.getUser();

        Post post = postRepository.getReferenceById(UUID.fromString(input.getPostId()));

        Comment comment = Comment.builder()
                .post(post)
                .member(memberEntity)
                .content(input.getContent())
                .build();

        commentRepository.save(comment);

        return CommentsCreateResponseDto.builder()
                .id(comment.getId().toString())
                .build();

    }
}

package com.deerear.app.service;

import ch.qos.logback.core.util.StringUtil;
import com.deerear.app.domain.Comment;
import com.deerear.app.domain.Member;
import com.deerear.app.domain.Post;
import com.deerear.app.dto.CommentsCreateRequestDto;
import com.deerear.app.dto.CommentsCreateResponseDto;
import com.deerear.app.dto.CommentsUpdateRequestDto;
import com.deerear.app.dto.CommentsUpdateResponseDto;
import com.deerear.app.repository.CommentRepository;
import com.deerear.app.repository.PostRepository;
import com.deerear.exception.BizException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.deerear.constant.ErrorCode.NOT_NULL;

@Service
@RequiredArgsConstructor
public class CommentsService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentsCreateResponseDto createComment(Object member, CommentsCreateRequestDto input) {

        // 입력값 검증
        if (StringUtil.isNullOrEmpty(input.getPostId())) {
            throw new BizException(null, NOT_NULL, "게시글ID");
        }


        Member memberEntity = (Member) member;

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

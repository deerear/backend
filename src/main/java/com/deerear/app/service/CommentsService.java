package com.deerear.app.service;

import com.deerear.app.domain.Comment;
import com.deerear.app.domain.Member;
import com.deerear.app.domain.Post;
import com.deerear.app.dto.CommentsCreateRequestDto;
import com.deerear.app.dto.CommentsCreateResponseDto;
import com.deerear.app.repository.CommentRepository;
import com.deerear.exception.BizException;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.util.StringUtil;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.deerear.constant.ErrorCode.NOT_NULL;

@Service
@RequiredArgsConstructor
public class CommentsService {

    private final CommentRepository repository;

    public CommentsCreateResponseDto createComment(Object member, CommentsCreateRequestDto input) {

        // TODO 입력값 검증 만들기

        Member memberEntity = (Member) member;

        Post post = Post.builder().id(UUID.fromString(input.getPostId())).build();

        Comment comment = Comment.builder()
                .post(post)
                .member(memberEntity)
                .content(input.getContent())
                .build();

        repository.save(comment);

        return CommentsCreateResponseDto.builder()
                .id(comment.getId().toString())
                .build();

    }
}

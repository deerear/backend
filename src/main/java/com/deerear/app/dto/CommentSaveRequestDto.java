package com.deerear.app.dto;

import com.deerear.app.domain.Comment;
import com.deerear.app.domain.Member;
import com.deerear.app.domain.Post;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentSaveRequestDto {

    @NotBlank(message = "올바르지 않은 게시글ID 입니다 ${validatedValue}")
    private String postId;

    @NotBlank(message = "올바르지 않은 댓글 입니다. ${validatedValue}")
    private String content;

    public Comment toEntity(Member member, Post post) {
        return Comment.builder()
                .member(member)
                .post(post)
                .content(content)
                .isDeleted(false)
                .build();
    }
}

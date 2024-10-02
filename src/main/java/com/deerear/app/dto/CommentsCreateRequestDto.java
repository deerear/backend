package com.deerear.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentsCreateRequestDto {

    @NotBlank(message = "올바르지 않은 게시글ID 입니다 ${validatedValue}")
    private String postId;

    @NotBlank(message = "올바르지 않은 댓글 입니다. ${validatedValue}")
    private String content;
}

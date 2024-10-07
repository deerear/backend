package com.deerear.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentListRequestDto extends PagingRequestDto {

    @NotBlank(message = "올바르지 않은 게시글ID 입니다 ${validatedValue}")
    private UUID postId;
}

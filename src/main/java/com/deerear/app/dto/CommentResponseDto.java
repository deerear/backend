package com.deerear.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto {

    private UUID commentId;
    private String nickName;
    private UUID postId;
    private String content;
    private long likeCount;
    private boolean isLiked;
    private LocalDateTime createdAt;
}

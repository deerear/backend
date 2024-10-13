package com.deerear.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentListResponseDto extends PagingResponseDto{

    private String commentId;
    private String nickName;
    private String postId;
    private String content;
    private long likeCount;
    private Boolean isLike;
}

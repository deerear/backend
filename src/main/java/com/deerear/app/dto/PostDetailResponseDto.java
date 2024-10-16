package com.deerear.app.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailResponseDto {

    private UUID postId;
    private String nickname;
    private String profileImg;
    private String title;
    private String content;
    private List<PostImageDto> postImgs;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Long commentCount;
    private Long likeCount;
    private Boolean isLike;
    private Date createdAt;

}

package com.deerear.app.dto;

import com.deerear.app.domain.Member;
import com.deerear.app.domain.Post;
import com.deerear.app.domain.PostImage;
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
public class PostResponseDto {

    private UUID postId;
    private String nickname;
    private String profileImg;
    private String title;
    private String content;
    private List<String> postImgs;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Long commentCount;
    private Long likeCount;
    private Boolean isLike;
    private Date createdAt;

}

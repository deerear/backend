package com.deerear.app.dto;

import com.deerear.app.domain.PostImage;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {

    private String title;
    private String content;
    private List<PostImage> postImgs;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Long commentCount;
    private Long likesCount;
}

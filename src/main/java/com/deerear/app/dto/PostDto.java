package com.deerear.app.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDto{

    private UUID id;
    private String title;
    private String content;
    private String thumbnail;
    private Long commentCount;
    private Long likesCount;
    private Boolean isLike;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Date createdAt;

}

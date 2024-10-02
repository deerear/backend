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

    public PostResponseDto toResponseDto(Post post, Member member, List<String> imageUrls, Boolean isLike){
        return PostResponseDto.builder()
                .postId(post.getId())
                .nickname(member.getNickname())
                .profileImg(member.getProfileImgUrl())
                .title(post.getTitle())
                .content(post.getContent())
                .postImgs(imageUrls)
                .latitude(post.getLatitude())
                .longitude(post.getLongitude())
                .commentCount(post.getCommentCount())
                .likeCount(post.getLikeCount())
                .createdAt(post.getCreatedAt())
                .isLike(isLike)
                .build();
    }
}

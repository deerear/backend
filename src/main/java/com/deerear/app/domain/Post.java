package com.deerear.app.domain;

import com.deerear.app.dto.PostDto;
import com.deerear.app.dto.PostResponseDto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString
@Table(name = "posts")
public class Post extends Likeable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false, unique = true)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column
    private String thumbnail;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "comment_count", nullable = false)
    private long commentCount;

    @Column(nullable = false)
    private Boolean isDeleted;

    @Override
    @Enumerated(EnumType.STRING)
    public TargetType getTargetType() {
        return TargetType.POST;
    }

    public void incrementCommentCount() {
        this.commentCount++;
    }

    public void decrementCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }

    public PostResponseDto toDto(Member member, List<String> imageUrls, Boolean isLike){
        return PostResponseDto.builder()
                .postId(id)
                .nickname(member.getNickname())
                .profileImg(member.getProfileImgUrl())
                .title(title)
                .content(content)
                .postImgs(imageUrls)
                .latitude(latitude)
                .longitude(longitude)
                .commentCount(commentCount)
                .likeCount(this.getLikeCount())
                .createdAt(this.getCreatedAt())
                .isLike(isLike)
                .build();
    }

    public PostDto toDto(Boolean isLike){
        return PostDto.builder()
                .id(id)
                .title(title)
                .content(content)
                .thumbnail(thumbnail)
                .commentCount(commentCount)
                .likesCount(this.getLikeCount())
                .isLike(isLike)
                .latitude(latitude)
                .longitude(longitude)
                .createdAt(this.getCreatedAt())
                .build();
    }
}
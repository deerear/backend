package com.deerear.app.dto;

import com.deerear.app.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberGetPostsResponseDto {
    private UUID id;         // 게시글 ID
    private String title;     // 게시글 제목
    private String content;   // 게시글 본문
    private Date createdAt;   // 게시글 작성 일시

    public static MemberGetPostsResponseDto toDto(UUID id, String title, String content, Date createdAt) {
        return new MemberGetPostsResponseDto(id, title, content, createdAt);
    }
}
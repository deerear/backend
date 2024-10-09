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

//@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberGetPostsResponseDto extends PagingResponseDto {
    // 게시글 ID
    private UUID id;
    // 게시글 제목
    private String title;
    // 게시글 본문
    private String content;
    // 게시글 작성 일시
    private Date createdAt;

//    public static MemberGetPostsResponseDto toDto(UUID id, String title, String content, Date createdAt,
//                                                  int size, int key, Boolean hasNext) {
//        return (MemberGetPostsResponseDto) MemberGetPostsResponseDto.builder()
//                .id(id)
//                .title(title)
//                .content(content)
//                .createdAt(createdAt)
//                .size(size)
//                .key(key)
//                .hasNext(hasNext)
//                .build();
//    }
}
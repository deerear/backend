package com.deerear.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberGetCommentssResponseDto {

    private UUID id;         // 댓글 ID
    private String content;   // 댓글 본문
    private Date createdAt;   // 게시글 작성 일시

    public static MemberGetCommentssResponseDto toDto(UUID id, String content, Date createdAt) {
        return new MemberGetCommentssResponseDto(id, content, createdAt);
    }
}

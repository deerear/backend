package com.deerear.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PagingResponseDto {
    private List<Object> objects;  // 게시글 리스트
    private int size;              // 페이지 크기
    private String key;            // 커서 값
    private boolean hasNext;       // 다음 페이지 존재 여부
}
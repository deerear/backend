package com.deerear.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PagingResponseDto {
    private int size;
    private String key;
    private Boolean hasNext;
}

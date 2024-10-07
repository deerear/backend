package com.deerear.app.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PagingRequestDto {

    @Min(value = 1, message = "페이지 사이즈는 1 이상이어야 합니다. ${validatedValue}")
    private int size;

    private String key;
}

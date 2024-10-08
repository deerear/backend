package com.deerear.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LikeSaveRequestDto {


    @NotBlank(message = "올바르지 않은 ID 입니다 ${validatedValue}")
    private String id;
}

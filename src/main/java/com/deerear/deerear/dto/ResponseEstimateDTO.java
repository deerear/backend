package com.deerear.deerear.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseEstimateDTO {

    @NotNull
    @Schema(description = "아이디", example = "아이디 입니다.")
    private Long id;

    @Schema(description = "이름", example = "이름 입니다.")
    private String name;
    private String description;
    private Double estimateValue;
}
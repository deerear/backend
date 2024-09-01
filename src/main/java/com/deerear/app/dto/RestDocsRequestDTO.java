package com.deerear.app.dto;


import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestDocsRequestDTO {

    private String name;
    private String description;
    @Nullable
    private Double estimateValue;
}

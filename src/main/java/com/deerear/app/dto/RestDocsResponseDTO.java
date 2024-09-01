package com.deerear.app.dto;


import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestDocsResponseDTO {

    @NotNull
    private Long id;

    private String name;
    private String description;
    private Double estimateValue;
}

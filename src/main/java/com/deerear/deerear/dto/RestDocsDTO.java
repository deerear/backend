package com.deerear.deerear.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RestDocsDTO {

    @NotNull
    private Long id;

    private String name;
    private String description;
    private Double estimateValue;
}

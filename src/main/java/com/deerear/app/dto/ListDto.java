package com.deerear.app.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ListDto {
    private List<Object> objects;
    private Boolean hasNext;
    private String nextKey;
    private Integer size;
}

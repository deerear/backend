package com.deerear.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class PostListRequestDto{

    private BigDecimal startLatitude;
    private BigDecimal startLongitude;
    private BigDecimal endLatitude;
    private BigDecimal endLongitude;

}

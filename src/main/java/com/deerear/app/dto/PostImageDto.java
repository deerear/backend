package com.deerear.app.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostImageDto {

    private UUID id;
    private String url;

}

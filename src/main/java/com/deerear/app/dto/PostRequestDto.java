package com.deerear.app.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Setter
@Getter
@ToString
@AllArgsConstructor
public class PostRequestDto {

    private String title;
    private String content;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private List<MultipartFile> postImgs;
}

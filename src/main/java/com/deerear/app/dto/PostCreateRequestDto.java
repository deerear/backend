package com.deerear.app.dto;

import com.deerear.app.domain.Member;
import com.deerear.app.domain.Post;
import jakarta.validation.constraints.Digits;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Setter
@Getter
@ToString
@AllArgsConstructor
public class PostCreateRequestDto {

    private String title;
    private String content;
    @Digits(integer = 10, fraction = 7)
    private BigDecimal latitude;
    @Digits(integer = 10, fraction = 7)
    private BigDecimal longitude;
    private List<MultipartFile> postImgs;

    public Post toEntity(Member member){
        return Post.builder()
                .title(title)
                .content(content)
                .member(member)
                .latitude(latitude)
                .longitude(longitude)
                .isDeleted(false)
                .build();
    }
}

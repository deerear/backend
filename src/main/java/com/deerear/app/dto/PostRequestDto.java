package com.deerear.app.dto;

import com.deerear.app.domain.Member;
import com.deerear.app.domain.Post;
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

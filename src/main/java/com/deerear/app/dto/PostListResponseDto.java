package com.deerear.app.dto;

import com.deerear.app.domain.Post;
import lombok.*;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostListResponseDto {
    private List<PostDto> objects;
    private Boolean hasNext;
    private String nextKey;
    private Integer size;

}

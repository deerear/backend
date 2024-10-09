package com.deerear.app.dto;

import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DmChatsResponseDto {

    private List<DmChatDto> objects;
    private Boolean hasNext;
    private String nextKey;
    private Integer size;

}

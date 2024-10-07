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
    private boolean hasNext;
    private String nextKey;
    private Integer size;

    @Getter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DmChatDto {

        private String nickname;
        private String profileImg;
        private String message;
        private Date createdAt;

    }
}

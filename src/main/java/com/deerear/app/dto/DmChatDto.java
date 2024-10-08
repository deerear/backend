package com.deerear.app.dto;

import lombok.*;

import java.util.Date;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DmChatDto {

    private String nickname;
    private String profileImg;
    private String message;
    private Date createdAt;

}

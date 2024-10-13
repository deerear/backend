package com.deerear.app.dto;

import lombok.*;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DmDto {

    private String nickname;
    private String message;

}


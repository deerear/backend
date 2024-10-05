package com.deerear.app.dto;

import lombok.*;

import java.util.UUID;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DmResponseDto {

    private UUID dmId;
}

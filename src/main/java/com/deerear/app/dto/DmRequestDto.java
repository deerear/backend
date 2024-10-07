package com.deerear.app.dto;

import com.deerear.app.domain.Dm;
import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DmRequestDto {

    private String dmMemberNickname;

    public Dm toEntity() {
        return Dm.builder()
                .lastMessage("")
                .build();
    }
}

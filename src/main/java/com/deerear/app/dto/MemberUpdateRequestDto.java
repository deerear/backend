package com.deerear.app.dto;

import com.deerear.app.domain.Member;
import com.deerear.app.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
@AllArgsConstructor
public class MemberUpdateRequestDto {
    private String nickname; // 업데이트할 닉네임
    private MultipartFile profileImg; // 프로필 이미지
}

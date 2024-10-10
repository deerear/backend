package com.deerear.app.domain;

import com.deerear.app.dto.DmChatDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "id", callSuper = false)
@Table(name = "dm_chats")
public class DmChat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false, unique = true)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Dm dm;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private String message;

    public DmChatDto toDto(){
        return DmChatDto.builder()
                .nickname(member.getNickname())
                .profileImg(member.getProfileImgUrl())
                .message(message)
                .createdAt(this.getCreatedAt())
                .build();
    }
}

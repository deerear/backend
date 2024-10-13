package com.deerear.app.domain;

import com.deerear.app.dto.DmResponseDto;
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
@Table(name = "dms")
public class Dm extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false, unique = true)
    private UUID id;

    private String lastMessage;

    public DmResponseDto toDto() {
        return DmResponseDto.builder()
                .dmId(id)
                .build();
    }
}

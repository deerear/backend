package com.deerear.app.domain;

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
@Table(name = "dm_members")
public class DmMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false, unique = true)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Dm dm;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private boolean isRead;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member chatMember;
}

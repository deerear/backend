package com.deerear.app.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString
@Table(name = "posts")
public class Post extends ModifiableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false, unique = true)
    private UUID id;

    @ManyToOne
    Member member;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column
    private BigDecimal latitude;

    @Column
    private BigDecimal longitude;

    @Column(name = "comment_count", nullable = false)
    private Long commentCount;

    @Column(name = "like_count", nullable = false)
    private Long likeCount;
}
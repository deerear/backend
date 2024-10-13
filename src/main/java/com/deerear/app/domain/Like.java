package com.deerear.app.domain;

import com.deerear.app.repository.CommentRepository;
import com.deerear.app.repository.PostRepository;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@Table(name = "likes")
public class Like extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private Likeable.TargetType targetType;

    @Column(name = "target_id", nullable = false)
    private UUID targetId;

    @Transient
    private Likeable target;

    public void loadTarget(PostRepository postRepository, CommentRepository commentRepository) {
        if (targetType == Likeable.TargetType.POST) {
            this.target = postRepository.getReferenceById(targetId);
        } else if (targetType == Likeable.TargetType.COMMENT) {
            this.target = commentRepository.getReferenceById(targetId);
        }
        // TODO repository를 여기서 DI하면 안되는교,,, @20241009 우지범
    }
}
package com.deerear.app.repository;

import com.deerear.app.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LikeRepository extends JpaRepository<Like, UUID> {

    boolean existsByMemberAndTargetTypeAndTargetId(Member member, Likeable.TargetType targetType, UUID targetId);

    Optional<Like> findByMemberAndTargetTypeAndTargetId(Member member, Likeable.TargetType targetType, UUID targetId);
}

package com.deerear.app.repository;

import com.deerear.app.domain.Like;
import com.deerear.app.domain.Member;
import com.deerear.app.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LikeRepository extends JpaRepository<Like, UUID> {

    boolean existsByMemberAndPost(Member member, Post post);
}

package com.deerear.app.repository;

import com.deerear.app.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
    //주석으로 import문 일단 제거.
   // Page<Post> findPostsByMember(Member member, PageRequest pageRequest);  // Page 반환
}

package com.deerear.app.repository;

import com.deerear.app.domain.Member;
import com.deerear.app.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    // 첫 페이지를 위한 메서드 (커서가 없을 때) - 페이징 처리 추가
    @Query("SELECT p FROM Post p WHERE p.member = :member ORDER BY p.createdAt DESC")
    List<Post> findFirstByMember(Member member, PageRequest pageRequest);

    // 커서를 기준으로 그 이후의 게시물을 가져오는 메서드 - 페이징 처리 추가
    @Query("SELECT p FROM Post p WHERE p.member = :member AND p.id > :lastPostId ORDER BY p.createdAt DESC")
    List<Post> findPostsByMemberAndIdGreaterThan(Member member, UUID lastPostId, PageRequest pageRequest);
}
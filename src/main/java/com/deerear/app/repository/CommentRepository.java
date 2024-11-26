package com.deerear.app.repository;

import com.deerear.app.domain.Comment;
import com.deerear.app.domain.Member;
import com.deerear.app.domain.Post;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    // 첫 페이지를 위한 메서드 (커서가 없을 때) - 페이징 처리 추가
    @Query("SELECT c FROM Comment c WHERE c.member = :member ORDER BY c.createdAt DESC")
    List<Comment> findFirstByMember(Member member, PageRequest pageRequest);

    @Query("SELECT c FROM Comment c " +
            "WHERE c.member = :member " +
            "AND c.createdAt < :lastCreatedAt " +
            "ORDER BY c.createdAt DESC")
    List<Comment> findCommentsByMemberAndCursor(
            Member member,
            Date lastCreatedAt,
            PageRequest pageRequest
    );
}

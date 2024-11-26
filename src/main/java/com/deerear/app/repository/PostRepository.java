package com.deerear.app.repository;


import com.deerear.app.domain.Dm;
import com.deerear.app.domain.DmChat;
import com.deerear.app.domain.Post;
import com.deerear.app.domain.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
  
    @Query("SELECT e FROM Post e WHERE e.createdAt < :createdAt And e.id != :nextKey AND e.latitude >= :startLatitude AND e.longitude >= :startLongitude AND e.latitude <= :endLatitude AND e.longitude <= :endLongitude AND e.isDeleted = false ORDER BY e.createdAt DESC")
    List<Post> findNextPage(@Param("createdAt") Date createdAt, @Param("nextKey") UUID nextKey, @Param("startLatitude") BigDecimal startLatitude, @Param("startLongitude") BigDecimal startLongitude,@Param("endLatitude") BigDecimal endLatitude, @Param("endLongitude") BigDecimal endLongitude, Pageable pageable);

    @Query("SELECT e FROM Post e WHERE  e.latitude >= :startLatitude AND e.longitude >= :startLongitude AND e.latitude <= :endLatitude AND e.longitude <= :endLongitude AND e.isDeleted = false ORDER BY e.createdAt DESC")
    List<Post> findNextPage(@Param("startLatitude") BigDecimal startLatitude, @Param("startLongitude") BigDecimal startLongitude,@Param("endLatitude") BigDecimal endLatitude, @Param("endLongitude") BigDecimal endLongitude, Pageable pageable);

    @Query("SELECT e FROM Post e " +
            "WHERE e.member = :member " +
            "AND e.isDeleted = false " +
            "AND e.createdAt < (SELECT p.createdAt FROM Post p WHERE p.id = :nextKey) " +
            "ORDER BY e.createdAt DESC")
    List<Post> findNextPage(@Param("member") Member member, @Param("nextKey") UUID nextKey, Pageable pageable);

    @Query("SELECT e FROM Post e WHERE e.member = :member And e.isDeleted = false ORDER BY e.createdAt DESC")
    List<Post> findNextPage(@Param("member") Member member, Pageable pageable);

    // 첫 페이지를 위한 메서드 (커서가 없을 때) - 페이징 처리 추가
    @Query("SELECT p FROM Post p WHERE p.member = :member ORDER BY p.createdAt DESC")
    List<Post> findFirstByMember(Member member, PageRequest pageRequest);

    // 커서를 기준으로 그 이후의 게시물을 가져오는 메서드 - 페이징 처리 추가
    @Query("SELECT p FROM Post p WHERE p.member = :member AND p.id > :lastPostId ORDER BY p.createdAt DESC")
    List<Post> findPostsByMemberAndIdGreaterThan(Member member, UUID lastPostId, PageRequest pageRequest);
}

package com.deerear.app.repository;

import com.deerear.app.domain.Dm;
import com.deerear.app.domain.DmChat;
import com.deerear.app.domain.Post;
import org.springframework.data.domain.Pageable;
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
    @Query("SELECT e FROM Post e WHERE e.latitude <= :startLatitude AND e.longitude <= :startLongitude AND e.latitude >= :endLatitude AND e.longitude >= :endLongitude")
    List<Post> findNextPage(@Param("startLatitude") BigDecimal startLatitude, @Param("startLongitude") BigDecimal startLongitude,@Param("endLatitude") BigDecimal endLatitude, @Param("endLongitude") BigDecimal endLongitude);

    //주석으로 import문 일단 제거.
   // Page<Post> findPostsByMember(Member member, PageRequest pageRequest);  // Page 반환
}

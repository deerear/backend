package com.deerear.deerear.repository;

import com.deerear.deerear.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);
    // 특정 username으로 회원이 존재하는지 확인하는 메서드
    boolean existsByUsername(String username);
}

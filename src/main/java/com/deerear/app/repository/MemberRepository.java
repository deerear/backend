package com.deerear.app.repository;

import com.deerear.app.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID> {
    Optional<Member> findByEmail(String email);
    // 특정 username으로 회원이 존재하는지 확인하는 메서드
    boolean existsByEmail(String email);
}

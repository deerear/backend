package com.deerear.app.repository;

import com.deerear.app.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByNickname(String nickname);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);
}

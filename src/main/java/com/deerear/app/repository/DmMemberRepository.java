package com.deerear.app.repository;

import com.deerear.app.domain.DmMember;
import com.deerear.app.domain.Member;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DmMemberRepository extends JpaRepository<DmMember, UUID> {
    boolean existsByMemberIdAndDmId(UUID memberId, UUID dmId);

    Optional<DmMember> findByMemberIdAndChatMemberId(UUID memberId, UUID chatMemberId);

    @Query("SELECT dmMember FROM DmMember dmMember WHERE dmMember.member = :member ORDER BY dmMember.dm.lastMessage DESC")
    List<DmMember> findDmsByMember(Member member, PageRequest pageRequest);

    @Query("SELECT dmMember FROM DmMember dmMember WHERE dmMember.member = :member AND dmMember.dm.id > :lastDmId ORDER BY dmMember.dm.lastMessage DESC")
    List<DmMember> findDmsByMemberAndIdGreaterThan(Member member, UUID lastDmId, PageRequest pageRequest);
}

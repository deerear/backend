package com.deerear.app.repository;

import com.deerear.app.domain.DmMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DmMemberRepository extends JpaRepository<DmMember, UUID> {
}

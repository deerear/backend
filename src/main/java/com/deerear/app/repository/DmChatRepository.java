package com.deerear.app.repository;

import com.deerear.app.domain.DmChat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DmChatRepository extends JpaRepository<DmChat, UUID> {

    List<DmChat> findAllByDmId(UUID dmId);
}

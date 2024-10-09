package com.deerear.app.repository;

import com.deerear.app.domain.Dm;
import com.deerear.app.domain.DmChat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface DmChatRepository extends JpaRepository<DmChat, UUID> {
    @Query("SELECT e FROM DmChat e WHERE e.createdAt > :lastTime And e.id != :lastId AND e.dm = :dm ORDER BY e.createdAt ASC")
    List<DmChat> findNextPage(@Param("lastTime") Date lastTime, @Param("lastId") UUID lastId, @Param("dm") Dm dm, Pageable pageable);

    @Query("SELECT e FROM DmChat e WHERE e.createdAt > :lastTime AND e.dm = :dm ORDER BY e.createdAt ASC")
    List<DmChat> findFirstPage(@Param("lastTime") Date lastTime, @Param("dm") Dm dm, Pageable pageable);

}

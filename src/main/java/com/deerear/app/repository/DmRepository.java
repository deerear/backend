package com.deerear.app.repository;

import com.deerear.app.domain.Dm;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DmRepository extends JpaRepository<Dm, UUID> {
}

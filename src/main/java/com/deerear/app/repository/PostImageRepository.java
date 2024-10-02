package com.deerear.app.repository;

import com.deerear.app.domain.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, UUID> {
    List<PostImage> findAllByPostId(UUID postId);
}

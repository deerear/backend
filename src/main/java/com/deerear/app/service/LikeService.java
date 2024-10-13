package com.deerear.app.service;

import com.deerear.app.domain.*;
import com.deerear.app.repository.CommentRepository;
import com.deerear.app.repository.LikeRepository;
import com.deerear.app.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LikeService {


    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public void likePost(Member member, UUID id) {
        likeOrUnlike(member, Likeable.TargetType.POST, id);
    }

    @Transactional
    public void likeComment(Member member, UUID id) {
        likeOrUnlike(member, Likeable.TargetType.COMMENT, id);
    }

    private void likeOrUnlike(Member member, Likeable.TargetType targetType, UUID targetId) {
        likeRepository.findByMemberAndTargetTypeAndTargetId(member, targetType, targetId).ifPresentOrElse(
                like -> {
                    likeRepository.delete(like);
                    like.loadTarget(postRepository, commentRepository);
                    like.getTarget().decrementLikeCount();
                },
                () -> {
                    Like like = Like.builder().member(member).targetType(targetType).targetId(targetId).build();
                    likeRepository.save(like);
                    like.loadTarget(postRepository, commentRepository);
                    like.getTarget().incrementLikeCount();
                }
        );
    }
}

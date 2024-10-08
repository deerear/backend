package com.deerear.app.service;

import com.deerear.app.domain.Comment;
import com.deerear.app.domain.Like;
import com.deerear.app.domain.Member;
import com.deerear.app.domain.Post;
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
        Post post = postRepository.getReferenceById(id);
        likeOrUnlike(member, post);
    }

    @Transactional
    public void likeComment(Member member, UUID id) {
        Comment comment = commentRepository.getReferenceById(id);
        likeOrUnlike(member, comment);
    }

    @Transactional(readOnly = true)
    public Boolean isLikedBy(Member member, Object entity) {
        if (entity instanceof Post post) {
            return likeRepository.existsByMemberAndPost(member, post);
        } else if (entity instanceof Comment comment) {
            return likeRepository.existsByMemberAndComment(member, comment);
        } else {
            return false;
        }
    }

    private void likeOrUnlike(Member member, Object entity) {
        if (entity instanceof Post post) {
            likeRepository.findByMemberAndPost(member, post).ifPresentOrElse(
                    likeRepository::delete,
                    () -> likeRepository.save(Like.builder().member(member).post(post).build())
            );
        } else if (entity instanceof Comment comment) {
            likeRepository.findByMemberAndComment(member, comment).ifPresentOrElse(
                    likeRepository::delete,
                    () -> likeRepository.save(Like.builder().member(member).comment(comment).build())
            );
        }
    }
}

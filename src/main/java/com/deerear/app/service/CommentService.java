package com.deerear.app.service;

import com.deerear.app.domain.Comment;
import com.deerear.app.domain.Member;
import com.deerear.app.domain.Post;
import com.deerear.app.dto.CommentListResponseDto;
import com.deerear.app.dto.CommentSaveRequestDto;
import com.deerear.app.dto.PagingRequestDto;
import com.deerear.app.repository.CommentRepository;
import com.deerear.app.repository.PostRepository;
import com.deerear.exception.BizException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.deerear.constant.ErrorCode.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    private final LikeService likeService;

    @Transactional
    public void createComment(Member member, CommentSaveRequestDto input) {

        // 입력값 검증
        final Post post = postRepository.findById(UUID.fromString(input.getPostId()))
                .orElseThrow(() -> new BizException(null, NOT_FOUND, "postId: " + input.getPostId()));

        commentRepository.save(input.toEntity(member, post));
    }

    @Transactional
    public void updateComment(Member member, CommentSaveRequestDto input, UUID commentId) {

        Comment comment = commentRepository.getReferenceById(commentId);
        validateCommentOwnership(member, comment);
        comment.updateContent(input.getContent());
    }

    @Transactional
    public void deleteComment(Member member, UUID commentId) {

        Comment comment = commentRepository.getReferenceById(commentId);
        validateCommentOwnership(member, comment);
        comment.isDeleted();
    }

    @Transactional(readOnly = true)
    public List<CommentListResponseDto> listComment(Member member, UUID postId, PagingRequestDto page) {

        int size = page.getSize();

        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));

        // Boolean isLikedByMember = likeService.isLikedBy(member, comment)

        // TODO @20241008 우지범: 1주일 소요 예정

        return new ArrayList<>();
    }

    private void validateCommentOwnership(Member member, Comment comment) {
        if (!member.equals(comment.getMember())) {
            throw new BizException(null, NOT_FOUND, "memberId: " + member.getId());
        }
    }
}

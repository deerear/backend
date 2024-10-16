package com.deerear.app.service;

import com.deerear.app.domain.Comment;
import com.deerear.app.domain.Member;
import com.deerear.app.domain.Post;
import com.deerear.app.dto.*;
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
import java.util.stream.Collectors;

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

    @Transactional(readOnly = true)
    public PagingResponseDto getComments(Member member, PagingRequestDto pagingRequestDto) {
        String lastPostId = pagingRequestDto.getKey();
        int size = pagingRequestDto.getSize();

        PageRequest pageRequest = PageRequest.of(0, size); // 페이징 처리

        List<Post> comments;
        if (lastPostId == null) {
            // 첫 페이지를 요청하는 경우
            comments = commentRepository.findFirstByMember(member, pageRequest);
        } else {
            // 커서를 기준으로 그 이후의 게시물을 가져옴
            comments = commentRepository.findCommentsByMemberAndIdGreaterThan(member, UUID.fromString(lastPostId), pageRequest);
        }

        boolean hasNext = comments.size() == size; // 다음 페이지 여부 확인

        List<Object> commentDtos = comments.stream()
                .map(post -> MemberGetCommentssResponseDto.toDto(
                        post.getId(),
                        post.getTitle(),
                        post.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return new PagingResponseDto(commentDtos, size, lastPostId, hasNext);
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

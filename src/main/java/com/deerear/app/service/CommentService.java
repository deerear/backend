package com.deerear.app.service;

import com.deerear.app.domain.Comment;
import com.deerear.app.domain.DmMember;
import com.deerear.app.domain.Member;
import com.deerear.app.domain.Post;
import com.deerear.app.dto.*;
import com.deerear.app.repository.CommentRepository;
import com.deerear.app.repository.PostRepository;
import com.deerear.app.util.KeyParser;
import com.deerear.exception.BizException;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.deerear.constant.ErrorCode.NOT_FOUND;
import static com.deerear.jooq.generated.Tables.COMMENTS;
import static com.deerear.jooq.generated.Tables.LIKES;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final DSLContext dsl;

    @Transactional
    public void createComment(Member member, CommentSaveRequestDto input) {

        Post post = postRepository.findById(UUID.fromString(input.getPostId()))
                .orElseThrow(() -> new BizException(null, NOT_FOUND, "postId: " + input.getPostId()));
        post.incrementCommentCount();
        commentRepository.save(input.toEntity(member, post));
    }

    @Transactional(readOnly = true)
    public PagingResponseDto getComments(Member member, PagingRequestDto pagingRequestDto) {
        int size = pagingRequestDto.getSize() != 0 ? pagingRequestDto.getSize() : 10;
        PageRequest pageRequest = PageRequest.of(0, size + 1);

        List<Comment> comments;
        String lastCommentId = pagingRequestDto.getKey();

        KeyParser.BasicKey basicKey = KeyParser.parseKey(lastCommentId);
        UUID idCursor = basicKey != null ? basicKey.id() : null;

        if (idCursor == null) {
            comments = commentRepository.findFirstByMember(member, pageRequest);
        } else {
            Comment lastComment = commentRepository.findById(basicKey.id())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid cursor"));

            comments = commentRepository.findCommentsByMemberAndCursor(
                    member,
                    lastComment.getCreatedAt(),
                    pageRequest
            );
        }

        boolean hasNext = comments.size() > size;
        List<Comment> responseComments = hasNext ? comments.subList(0, size) : comments;

        List<Object> commentDtos = responseComments.stream()
                .map(comment -> MemberGetCommentsResponseDto.toDto(
                        comment.getId(),
                        comment.getContent(),
                        comment.getCreatedAt()
                ))
                .collect(Collectors.toList());

        String nextKey = hasNext ? responseComments.get(size - 1).getId().toString() : null;

        return new PagingResponseDto(commentDtos, size, nextKey, hasNext);
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
        comment.getPost().decrementCommentCount();
        comment.isDeleted();
    }

    @Transactional(readOnly = true)
    public PagingResponseDto listComment(Member member, UUID postId, PagingRequestDto page) {

        int size = page.getSize()+1;
        String key = page.getKey();

        KeyParser.BasicKey basicKey = KeyParser.parseKey(key);
        LocalDateTime createdAtCursor = basicKey != null ? basicKey.createdAt() : null;
        UUID idCursor = basicKey != null ? basicKey.id() : null;

        // WHERE
        var condition = COMMENTS.POST_ID.eq(postId);

        // AND KEY
        if (basicKey != null) {
            condition = condition.and(
                    COMMENTS.CREATED_AT.gt(createdAtCursor).or(
                            COMMENTS.CREATED_AT.eq(createdAtCursor).and(
                                    COMMENTS.ID.gt(idCursor)
                            )
                    )
            );
        }

        // SUB - SELECT
        var isLikedCondition = DSL.select(DSL.field("1"))
                .from(LIKES)
                .where(
                        LIKES.MEMBER_ID.eq(member.getId()),
                        LIKES.TARGET_ID.eq(COMMENTS.ID),
                        LIKES.TARGET_TYPE.eq("COMMENT")
                )
                .asField("isLiked");

        /*
        쿼리 성능 테스트 필요
        var isLikedCondition = DSL.when(
        DSL.exists(
            DSL.selectOne()
               .from(LIKES)
               .where(
                   LIKES.MEMBER_ID.eq(member.getId()),
                   LIKES.TARGET_ID.eq(COMMENTS.ID),
                   LIKES.TARGET_TYPE.eq("COMMENT")
               )
        ), DSL.trueValue()).otherwise(DSL.falseValue()).as("isLiked");
         */

        // 메인 쿼리 실행
        List<CommentResponseDto> comments = dsl.select(
                        COMMENTS.ID,
                        COMMENTS.members().NICKNAME,
                        COMMENTS.POST_ID,
                        COMMENTS.CONTENT,
                        COMMENTS.LIKE_COUNT,
                        COMMENTS.CREATED_AT,
                        isLikedCondition
                )
                .from(COMMENTS)
                .where(condition)
                .orderBy(COMMENTS.CREATED_AT.asc(), COMMENTS.ID.asc())
                .limit(size)
                .fetch(record -> CommentResponseDto.builder()
                        .commentId(record.get(COMMENTS.ID))
                        .nickName(record.get(COMMENTS.members().NICKNAME))
                        .postId(record.get(COMMENTS.POST_ID))
                        .content(record.get(COMMENTS.CONTENT))
                        .likeCount(record.get(COMMENTS.LIKE_COUNT))
                        .isLiked(record.get("isLiked", Integer.class) != null)
                        .createdAt(record.get(COMMENTS.CREATED_AT))
                        .build());

        boolean hasNext = false;
        String nextKey = null;

        if (comments.size() == size) {
            hasNext = true;
            CommentResponseDto lastComment = comments.remove(size - 1);

            nextKey = lastComment.getCreatedAt().toString() + "_" + lastComment.getCommentId().toString();
        }

        return PagingResponseDto.of(Collections.singletonList(comments), size, nextKey, hasNext);
    }

    private void validateCommentOwnership(Member member, Comment comment) {
        if (!member.equals(comment.getMember())) {
            throw new BizException(null, NOT_FOUND, "memberId: " + member.getId());
        }
    }
    
}

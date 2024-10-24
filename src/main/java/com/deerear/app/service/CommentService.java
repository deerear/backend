package com.deerear.app.service;

import com.deerear.app.domain.Comment;
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

        // 입력값 검증
        final Post post = postRepository.findById(UUID.fromString(input.getPostId()))
                .orElseThrow(() -> new BizException(null, NOT_FOUND, "postId: " + input.getPostId()));

        commentRepository.save(input.toEntity(member, post));
    }

    @Transactional(readOnly = true)
    public PagingResponseDto getComments(Member member, PagingRequestDto pagingRequestDto) {
        String lastPostId = pagingRequestDto.getKey();
        //int size = pagingRequestDto.getSize();

        PageRequest pageRequest = PageRequest.of(0, 10); // 페이징 처리

        List<Post> comments;
        if (lastPostId == null) {
            // 첫 페이지를 요청하는 경우
            comments = commentRepository.findFirstByMember(member, pageRequest);
        } else {
            // 커서를 기준으로 그 이후의 게시물을 가져옴
            comments = commentRepository.findCommentsByMemberAndIdGreaterThan(member, UUID.fromString(lastPostId), pageRequest);
        }

        boolean hasNext = comments.size() == 10; // 다음 페이지 여부 확인

        List<Object> commentDtos = comments.stream()
                .map(post -> MemberGetCommentssResponseDto.toDto(
                        post.getId(),
                        post.getTitle(),
                        post.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return new PagingResponseDto(commentDtos, 10, lastPostId, hasNext);
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

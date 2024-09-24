package com.deerear.app.service;

import com.deerear.app.domain.Comment;
import com.deerear.app.domain.Member;
import com.deerear.app.domain.Post;
import com.deerear.app.dto.CommentsCreateRequestDto;
import com.deerear.app.dto.CommentsCreateResponseDto;
import com.deerear.app.repository.CommentRepository;
import com.deerear.app.repository.PostRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentsServiceTest {

    private CommentRepository commentRepository;
    private CommentsService commentsService;
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        commentRepository = mock(CommentRepository.class);
        postRepository = mock(PostRepository.class);

        commentsService = new CommentsService(commentRepository, postRepository);
    }

    @AfterEach
    void tearDown() {
        reset(commentRepository);
    }

    @Test
    void createComment() {

        // NOTE GPT가 만듬ㅎ

        UUID postId = UUID.randomUUID();
        String content = "This is a test comment.";
        CommentsCreateRequestDto requestDto = new CommentsCreateRequestDto(postId.toString(), content);

        Member member = Member.builder().id(postId).nickname("testuser").build();

        Post post = Post.builder().id(postId).title("testuser").content(content).build();

        when(postRepository.getReferenceById(postId)).thenReturn(post);

        // Mock the behavior of repository.save()
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment comment = invocation.getArgument(0);
            // Simulate ID generation upon saving
            comment.setId(UUID.randomUUID());
            return comment;
        });

        // Act
        CommentsCreateResponseDto responseDto = commentsService.createComment(member, requestDto);

        // Assert
        assertNotNull(responseDto);
        assertNotNull(responseDto.getId());

        // repository.save()가 한 번 호출되었는지 검증하고, 전달된 Comment 객체 캡처
        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository, times(1)).save(commentCaptor.capture());
        Comment savedComment = commentCaptor.getValue();

        // 저장된 Comment 객체의 내용 검증
        assertNotNull(savedComment);
        assertEquals(content, savedComment.getContent());
        assertEquals(member, savedComment.getMember());
        assertEquals(post, savedComment.getPost());

        System.out.println("====================== 예 테스트 끝났습니다 ======================");
        System.out.println(savedComment);
    }
}
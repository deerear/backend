package com.deerear.app.service;

import com.deerear.app.domain.Comment;
import com.deerear.app.domain.Member;
import com.deerear.app.dto.CommentsCreateRequestDto;
import com.deerear.app.dto.CommentsCreateResponseDto;
import com.deerear.app.repository.CommentRepository;
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

    @BeforeEach
    void setUp() {
        commentRepository = mock(CommentRepository.class);

        commentsService = new CommentsService(commentRepository);
    }

    @AfterEach
    void tearDown() {
        reset(commentRepository);
    }

    @Test
    void createComment() {

        // NOTE GPT가 만듬ㅎ

        // Arrange
        UUID postId = UUID.randomUUID();
        String content = "This is a test comment.";
        CommentsCreateRequestDto requestDto = new CommentsCreateRequestDto(postId.toString(), content);

        // Create a mock member
        Member member = Member.builder().id(postId).nickname("testuser").build();

        // Capture the Comment object passed to repository.save()
        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);

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

        // Verify that repository.save() was called once
        verify(commentRepository, times(1)).save(commentCaptor.capture());

        // Get the Comment object that was saved
        Comment savedComment = commentCaptor.getValue();

        // Verify the content of the saved Comment
        assertNotNull(savedComment);
        assertEquals(content, savedComment.getContent());
        assertEquals(member, savedComment.getMember());
        assertEquals(postId, savedComment.getPost().getId());

        System.out.println("====================== 예 테스트 끝났습니다 ======================");
        System.out.println(savedComment);
    }
}
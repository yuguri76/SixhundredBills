package com.sparta.sixhundredbills.comment_like.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.auth.repository.UserRepository;
import com.sparta.sixhundredbills.comment.entity.Comment;
import com.sparta.sixhundredbills.comment.repository.CommentRepository;
import com.sparta.sixhundredbills.comment_like.dto.CommentLikeResponseDto;
import com.sparta.sixhundredbills.comment_like.entity.CommentLike;
import com.sparta.sixhundredbills.comment_like.repository.CommentLikeRepository;
import com.sparta.sixhundredbills.exception.CustomException;
import com.sparta.sixhundredbills.exception.ErrorEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentLikeServiceTest {

    @Mock
    private CommentLikeRepository commentLikeRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JPAQueryFactory queryFactory;

    @InjectMocks
    private CommentLikeService commentLikeService;

    private User user1;
    private User user2;
    private Comment commentByUser2;

    /**
     * 각 테스트 실행 전 초기 설정 메서드
     * Mock 객체 초기화 및 테스트 데이터 설정
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 첫 번째 사용자 설정
        user1 = new User();
        user1.setId(1L);
        user1.setEmail("user1@example.com");

        // 두 번째 사용자 설정
        user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@example.com");

        // 두 번째 사용자가 작성한 댓글 설정
        commentByUser2 = new Comment();
        commentByUser2.setId(1L);
        commentByUser2.setUser(user2);
        commentByUser2.setComment("This is a comment by user2.");
    }

    /**
     * 댓글 좋아요 성공 테스트
     * @throws Exception 예외 발생 시
     */
    @Test
    void likeComment_Success() {
        Long postId = 1L;
        Long commentId = 1L;

        // Mock 객체 설정
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(commentByUser2));
        when(commentLikeRepository.findByUserAndComment(user1, commentByUser2)).thenReturn(Optional.empty());
        when(commentLikeRepository.save(any(CommentLike.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 서비스 호출 및 검증
        CommentLikeResponseDto responseDto = commentLikeService.likeComment(postId, commentId, user1);

        assertNotNull(responseDto);
        assertEquals("성공적으로 좋아요를 등록했습니다", responseDto.getContent());
        verify(commentLikeRepository, times(1)).save(any(CommentLike.class));
    }

    /**
     * 이미 좋아요한 댓글에 대해 다시 좋아요 시도할 때의 테스트
     * @throws Exception 예외 발생 시
     */
    @Test
    void likeComment_AlreadyLiked() {
        Long postId = 1L;
        Long commentId = 1L;
        CommentLike commentLike = CommentLike.builder()
                .user(user1)
                .comment(commentByUser2)
                .build();

        // Mock 객체 설정
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(commentByUser2));
        when(commentLikeRepository.findByUserAndComment(user1, commentByUser2)).thenReturn(Optional.of(commentLike));

        // 서비스 호출 및 검증
        CustomException exception = assertThrows(CustomException.class, () -> commentLikeService.likeComment(postId, commentId, user1));

        assertEquals(ErrorEnum.COMMENT_ALREADY_LIKED, exception.getErrorEnum());
    }

    /**
     * 사용자가 좋아요한 댓글 목록 조회 성공 테스트
     * @throws Exception 예외 발생 시
     */
    @Test
    void getLikedComments_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        CommentLike commentLike = CommentLike.builder()
                .user(user1)
                .comment(commentByUser2)
                .build();
        Page<CommentLike> likedComments = new PageImpl<>(List.of(commentLike));

        // Mock 객체 설정
        when(commentLikeRepository.findAllByUser(user1, pageable)).thenReturn(likedComments);

        // 서비스 호출 및 검증
        Page<CommentLikeResponseDto> responseDtos = commentLikeService.getLikedComments(user1, pageable);

        assertNotNull(responseDtos);
        assertEquals(1, responseDtos.getTotalElements());
        assertEquals("This is a comment by user2.", responseDtos.getContent().get(0).getContent());
    }

    /**
     * 자신의 댓글에 좋아요 시도할 때의 테스트
     * @throws Exception 예외 발생 시
     */
    @Test
    void likeComment_OwnComment() {
        Long postId = 1L;
        Long commentId = 1L;

        // user1이 자신의 댓글을 좋아요하려고 시도
        commentByUser2.setUser(user1);

        // Mock 객체 설정
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(commentByUser2));

        // 서비스 호출 및 검증
        CustomException exception = assertThrows(CustomException.class, () -> commentLikeService.likeComment(postId, commentId, user1));

        assertEquals(ErrorEnum.CANNOT_LIKE_OWN_COMMENT, exception.getErrorEnum());
    }
}

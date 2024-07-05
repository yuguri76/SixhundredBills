package com.sparta.sixhundredbills.post_like.service;

import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.exception.CustomException;
import com.sparta.sixhundredbills.exception.ErrorEnum;
import com.sparta.sixhundredbills.post.entity.Post;
import com.sparta.sixhundredbills.post.repository.PostRepository;
import com.sparta.sixhundredbills.post_like.dto.PostLikeResponseDto;
import com.sparta.sixhundredbills.post_like.entity.PostLike;
import com.sparta.sixhundredbills.post_like.repository.PostLikeRepository;
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

class PostLikeServiceTest {

    @Mock
    private PostLikeRepository postLikeRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostLikeService postLikeService;

    private User user1;
    private User user2;
    private Post post;

    /**
     * 각 테스트 실행 전에 초기 설정을 수행합니다.
     * 사용자 및 게시물 객체를 초기화하고 Mockito Mock 객체를 설정합니다.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 첫 번째 사용자 초기화
        user1 = new User();
        user1.setId(1L);
        user1.setEmail("user1@example.com");

        // 두 번째 사용자 초기화
        user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@example.com");

        // 첫 번째 사용자가 작성한 게시물 초기화
        post = new Post();
        post.setId(1L);
        post.setUser(user1);
        post.setContent("Test Content");
    }

    /**
     * 게시물에 좋아요를 성공적으로 추가하는 테스트
     */
    @Test
    void likePost_Success() {
        Long postId = post.getId();

        // Mock 설정
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postLikeRepository.findByUserAndPost(user2, post)).thenReturn(Optional.empty());
        when(postLikeRepository.save(any(PostLike.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 서비스 메서드 호출 및 검증
        PostLikeResponseDto responseDto = postLikeService.likePost(postId, user2);

        assertNotNull(responseDto);
        assertEquals("성공적으로 좋아요를 등록했습니다", responseDto.getContent());
        verify(postLikeRepository, times(1)).save(any(PostLike.class));
    }

    /**
     * 이미 좋아요를 누른 게시물에 다시 좋아요를 시도할 때의 테스트
     */
    @Test
    void likePost_AlreadyLiked() {
        Long postId = post.getId();
        PostLike postLike = new PostLike();
        postLike.setUser(user2);
        postLike.setPost(post);

        // Mock 설정
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postLikeRepository.findByUserAndPost(user2, post)).thenReturn(Optional.of(postLike));

        // 서비스 메서드 호출 및 검증
        CustomException exception = assertThrows(CustomException.class, () -> postLikeService.likePost(postId, user2));

        assertEquals(ErrorEnum.POST_ALREADY_LIKED, exception.getErrorEnum());
    }

    /**
     * 게시물에 좋아요를 성공적으로 취소하는 테스트
     */
    @Test
    void unlikePost_Success() {
        Long postId = post.getId();
        PostLike postLike = new PostLike();
        postLike.setUser(user2);
        postLike.setPost(post);

        // Mock 설정
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postLikeRepository.findByUserAndPost(user2, post)).thenReturn(Optional.of(postLike));

        // 서비스 메서드 호출 및 검증
        PostLikeResponseDto responseDto = postLikeService.unlikePost(postId, user2);

        assertNotNull(responseDto);
        assertEquals("성공적으로 좋아요를 취소했습니다", responseDto.getContent());
        verify(postLikeRepository, times(1)).delete(postLike);
    }

    /**
     * 사용자가 좋아요를 누른 게시물 목록을 성공적으로 조회하는 테스트
     */
    @Test
    void getLikedPosts_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        PostLike postLike = new PostLike();
        postLike.setPost(post);
        Page<PostLike> likedPosts = new PageImpl<>(List.of(postLike));

        // Mock 설정
        when(postLikeRepository.findAllByUser(user2, pageable)).thenReturn(likedPosts);

        // 서비스 메서드 호출 및 검증
        Page<PostLikeResponseDto> responseDtoPage = postLikeService.getLikedPosts(user2, pageable);

        assertNotNull(responseDtoPage);
        assertEquals(1, responseDtoPage.getTotalElements());
        assertEquals("Test Content", responseDtoPage.getContent().get(0).getContent());
    }
}

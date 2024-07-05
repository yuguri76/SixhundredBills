package com.sparta.sixhundredbills.post_like.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.auth.jwt.JwtUtil;
import com.sparta.sixhundredbills.auth.repository.UserRepository;
import com.sparta.sixhundredbills.auth.security.UserDetailsImpl;
import com.sparta.sixhundredbills.auth.security.UserDetailsServiceImpl;
import com.sparta.sixhundredbills.post_like.dto.PostLikeRequestDto;
import com.sparta.sixhundredbills.post_like.dto.PostLikeResponseDto;
import com.sparta.sixhundredbills.post_like.service.PostLikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PostLikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostLikeService postLikeService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private User user;
    private UserDetailsImpl userDetails;
    private String jwtToken;

    /**
     * 각 테스트 실행 전 초기 설정 메서드
     * Mock 객체 초기화 및 테스트 데이터 설정
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // 사용자 객체 생성
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setPassword("password");

        // UserDetailsImpl 객체 생성 및 모킹 설정
        userDetails = new UserDetailsImpl(user);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails);

        // JWT 토큰 생성 및 모킹 설정
        jwtToken = "Bearer mockToken";
        when(jwtUtil.createToken(user.getEmail(), 1800000)).thenReturn(jwtToken);

        // SecurityContext에 UserDetails 설정
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
    }

    /**
     * 게시물 좋아요 성공 테스트
     * @throws Exception 예외 발생 시
     */
    @Test
    void likePost_Success() throws Exception {
        Long postId = 1L;
        PostLikeRequestDto requestDto = new PostLikeRequestDto();
        requestDto.setUserId(1L);
        PostLikeResponseDto responseDto = PostLikeResponseDto.builder()
                .content("성공적으로 좋아요를 등록했습니다")
                .postId(postId)
                .userId(1L)
                .likesCount(1)
                .build();

        // Mock 객체 설정
        when(postLikeService.likePost(any(Long.class), any())).thenReturn(responseDto);

        // API 호출 및 검증
        mockMvc.perform(MockMvcRequestBuilders.post("/posts/{postId}/likes", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .header("Authorization", jwtToken))
                .andExpect(status().isCreated())  // Expect 201 for creation
                .andExpect(jsonPath("$.content").value("성공적으로 좋아요를 등록했습니다"));
    }

    /**
     * 게시물 좋아요 취소 성공 테스트
     * @throws Exception 예외 발생 시
     */
    @Test
    void unlikePost_Success() throws Exception {
        Long postId = 1L;
        PostLikeRequestDto requestDto = new PostLikeRequestDto();
        requestDto.setUserId(1L);
        PostLikeResponseDto responseDto = PostLikeResponseDto.builder()
                .content("성공적으로 좋아요를 취소했습니다")
                .postId(postId)
                .userId(1L)
                .likesCount(0)
                .build();

        // Mock 객체 설정
        when(postLikeService.unlikePost(any(Long.class), any())).thenReturn(responseDto);

        // API 호출 및 검증
        mockMvc.perform(MockMvcRequestBuilders.delete("/posts/{postId}/likes", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .header("Authorization", jwtToken))
                .andExpect(status().isCreated())  // Expect 201 for deletion
                .andExpect(jsonPath("$.content").value("성공적으로 좋아요를 취소했습니다"));
    }

    /**
     * 사용자가 좋아요한 게시물 목록 조회 성공 테스트
     * @throws Exception 예외 발생 시
     */
    @Test
    void getLikedPosts_Success() throws Exception {
        PostLikeResponseDto responseDto = PostLikeResponseDto.builder()
                .content("Test Post")
                .postId(1L)
                .userId(1L)
                .likesCount(1)
                .build();

        List<PostLikeResponseDto> likedPosts = Collections.singletonList(responseDto);
        Page<PostLikeResponseDto> likedPostsPage = new PageImpl<>(likedPosts);
        Pageable pageable = PageRequest.of(0, 10);

        // Mock 객체 설정
        when(postLikeService.getLikedPosts(any(User.class), any(Pageable.class))).thenReturn(likedPostsPage);

        // API 호출 및 검증
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/likes")
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk())  // Expect 200 for retrieval
                .andExpect(jsonPath("$.content[0].content").value("Test Post"));
    }
}

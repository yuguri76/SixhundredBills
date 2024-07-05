package com.sparta.sixhundredbills.comment_like.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.auth.jwt.JwtUtil;
import com.sparta.sixhundredbills.auth.repository.UserRepository;
import com.sparta.sixhundredbills.auth.security.UserDetailsImpl;
import com.sparta.sixhundredbills.auth.security.UserDetailsServiceImpl;
import com.sparta.sixhundredbills.comment_like.dto.CommentLikeRequestDto;
import com.sparta.sixhundredbills.comment_like.dto.CommentLikeResponseDto;
import com.sparta.sixhundredbills.comment_like.service.CommentLikeService;
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
class CommentLikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentLikeService commentLikeService;

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
     * 초기 설정 메서드
     * 각 테스트가 실행되기 전에 호출되어 사용자, JWT 토큰 등을 설정
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // 목 객체 초기화
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
     * 댓글 좋아요 테스트 - 성공
     * 댓글에 성공적으로 좋아요를 등록하는 경우
     * @throws Exception 예외 발생 시
     */
    @Test
    void likeComment_Success() throws Exception {
        Long postId = 1L;
        Long commentId = 1L;
        CommentLikeRequestDto requestDto = new CommentLikeRequestDto();
        requestDto.setUserId(1L);
        CommentLikeResponseDto responseDto = CommentLikeResponseDto.builder()
                .content("성공적으로 좋아요를 등록했습니다")
                .commentId(commentId)
                .userId(1L)
                .likesCount(1)
                .build();

        // 서비스 메서드 모킹
        when(commentLikeService.likeComment(any(Long.class), any(Long.class), any())).thenReturn(responseDto);

        // MockMvc를 사용하여 POST 요청을 보내고 기대하는 상태 및 결과 검증
        mockMvc.perform(MockMvcRequestBuilders.post("/posts/{postId}/comments/{commentId}/likes", postId, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .header("Authorization", jwtToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("성공적으로 좋아요를 등록했습니다"));
    }

    /**
     * 댓글 좋아요 취소 테스트 - 성공
     * 댓글에 성공적으로 좋아요를 취소하는 경우
     * @throws Exception 예외 발생 시
     */
    @Test
    void unlikeComment_Success() throws Exception {
        Long postId = 1L;
        Long commentId = 1L;
        CommentLikeRequestDto requestDto = new CommentLikeRequestDto();
        requestDto.setUserId(1L);
        CommentLikeResponseDto responseDto = CommentLikeResponseDto.builder()
                .content("성공적으로 좋아요를 취소했습니다")
                .commentId(commentId)
                .userId(1L)
                .likesCount(0)
                .build();

        // 서비스 메서드 모킹
        when(commentLikeService.unlikeComment(any(Long.class), any(Long.class), any())).thenReturn(responseDto);

        // MockMvc를 사용하여 DELETE 요청을 보내고 기대하는 상태 및 결과 검증
        mockMvc.perform(MockMvcRequestBuilders.delete("/posts/{postId}/comments/{commentId}/likes", postId, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .header("Authorization", jwtToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("성공적으로 좋아요를 취소했습니다"));  // JSON 응답 내용 검증
    }

    /**
     * 사용자가 좋아요한 댓글 목록 조회 테스트 - 성공
     * 사용자가 좋아요한 댓글 목록을 성공적으로 조회하는 경우
     * @throws Exception 예외 발생 시
     */
    @Test
    void getLikedComments_Success() throws Exception {
        CommentLikeResponseDto responseDto = CommentLikeResponseDto.builder()
                .content("Test Comment")
                .commentId(1L)
                .userId(1L)
                .likesCount(1)
                .build();

        List<CommentLikeResponseDto> likedComments = Collections.singletonList(responseDto);
        Page<CommentLikeResponseDto> likedCommentsPage = new PageImpl<>(likedComments);
        Pageable pageable = PageRequest.of(0, 10);

        // 서비스 메서드 모킹
        when(commentLikeService.getLikedComments(any(User.class), any(Pageable.class))).thenReturn(likedCommentsPage);

        // MockMvc를 사용하여 GET 요청을 보내고 기대하는 상태 및 결과 검증
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/comments/likes")
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].content").value("Test Comment")); // JSON 응답 내용 검증
    }
}

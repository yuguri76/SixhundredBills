package com.sparta.sixhundredbills.post.service;

import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.exception.NotFoundPostException;
import com.sparta.sixhundredbills.exception.UnauthorizedException;
import com.sparta.sixhundredbills.post.dto.PostRequestDto;
import com.sparta.sixhundredbills.post.dto.PostResponseDto;
import com.sparta.sixhundredbills.post.entity.Post;
import com.sparta.sixhundredbills.post.repository.PostRepository;
import com.sparta.sixhundredbills.util.AnonymousNameGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    /**
     * 게시물을 생성하는 메서드
     * @param postRequestDto 게시물 생성 요청 DTO
     * @param user 작성자 정보
     * @return 생성된 게시물 정보
     */
    public PostResponseDto createPost(PostRequestDto postRequestDto, User user) {
        // 카테고리 유효성 검사
        if (!postRequestDto.isValidCategory()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "카테고리에 일상공유, 고민상담 2개의 카테고리 중 하나를 입력해주세요.");
        }

        // 익명 닉네임 생성
        String anonymousName = AnonymousNameGenerator.generate();

        // Post 객체를 빌더 패턴을 사용하여 생성
        Post post = Post.builder()
                .user(user)
                .showName(anonymousName) // 익명 닉네임 설정
                .content(postRequestDto.getContent())
                .category(postRequestDto.getCategory())
                .likeCount(0)
                .build();
        // 생성된 Post 객체를 데이터베이스에 저장
        postRepository.save(post);
        // 저장된 Post 객체를 기반으로 PostResponseDto를 생성하여 반환
        return new PostResponseDto(post);
    }

    /**
     * 모든 게시물을 페이지네이션하여 조회하는 메서드
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 페이지네이션된 게시물 목록
     */
    public Page<PostResponseDto> getPosts(int page, int size) {
        // 페이지네이션 설정 (페이지 번호와 페이지 크기, 정렬 기준)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        // 설정된 페이지네이션과 정렬 기준으로 게시물 목록 조회
        Page<Post> posts = postRepository.findAll(pageable);
        // 조회된 게시물 목록을 PostResponseDto로 변환하여 반환
        return posts.map(PostResponseDto::new);
    }

    /**
     * 게시물을 수정하는 메서드
     * @param postId 수정할 게시물 ID
     * @param postRequestDto 게시물 수정 요청 DTO
     * @param user 인증된 사용자 정보
     * @return 수정된 게시물 정보
     */
    public PostResponseDto updatePost(Long postId, PostRequestDto postRequestDto, User user) {
        // 게시물 ID로 게시물을 조회하고, 존재하지 않으면 예외 발생
        Post post = postRepository.findById(postId)
                .orElseThrow(NotFoundPostException::new);

        // 게시물 작성자와 현재 사용자가 일치하지 않으면 예외 발생
        if (!post.getUser().equals(user)) {
            throw new UnauthorizedException("작성자 또는 관리자만 수정할 수 있습니다.");
        }

        // 카테고리 유효성 검사
        if (!postRequestDto.isValidCategory()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "카테고리에 일상공유, 고민상담 2개의 카테고리 중 하나를 입력해주세요.");
        }

        // 게시물의 내용을 수정
        post.setShowName(postRequestDto.getShowName());
        post.setContent(postRequestDto.getContent());
        post.setCategory(postRequestDto.getCategory());
        post.setUpdatedAt(LocalDateTime.now());

        // 수정된 게시물을 데이터베이스에 저장
        postRepository.save(post);
        // 수정된 게시물을 기반으로 PostResponseDto를 생성하여 반환
        return new PostResponseDto(post);
    }

    /**
     * 게시물을 삭제하는 메서드
     * @param postId 삭제할 게시물 ID
     * @param user 인증된 사용자 정보
     */
    public void deletePost(Long postId, User user) {
        // 게시물 ID로 게시물을 조회하고, 존재하지 않으면 예외 발생
        Post post = postRepository.findById(postId)
                .orElseThrow(NotFoundPostException::new);

        // 게시물 작성자와 현재 사용자가 일치하지 않으면 예외 발생
        if (!post.getUser().equals(user)) {
            throw new UnauthorizedException("작성자 또는 관리자만 삭제할 수 있습니다.");
        }

        // 게시물을 데이터베이스에서 삭제
        postRepository.delete(post);
    }
}
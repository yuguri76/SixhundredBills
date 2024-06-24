package com.sparta.sixhundredbills.post.service;

import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.auth.entity.Role;
import com.sparta.sixhundredbills.exception.NotFoundPostException;
import com.sparta.sixhundredbills.exception.UnauthorizedException;
import com.sparta.sixhundredbills.post.dto.PostRequestDto;
import com.sparta.sixhundredbills.post.dto.PostResponseDto;
import com.sparta.sixhundredbills.post.entity.Post;
import com.sparta.sixhundredbills.post_like.repository.PostLikeRepository;
import com.sparta.sixhundredbills.post.repository.PostRepository;
import com.sparta.sixhundredbills.util.AnonymousNameGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PostService {

    /**
     * 게시물 생성
     * @param postRequestDto 생성할 게시물의 정보
     * @param user 게시물을 작성한 사용자
     * @return 생성된 게시물의 응답 데이터
     */
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    public PostResponseDto createPost(PostRequestDto postRequestDto, User user) {
        if (!postRequestDto.isValidCategory()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "카테고리에 일상공유, 고민상담, 익명토론 중 하나를 입력해주세요.");
        }

        String anonymousName = AnonymousNameGenerator.generate();

        Post post = Post.builder()
                .user(user)
                .showName(anonymousName)
                .content(postRequestDto.getContent())
                .category(postRequestDto.getCategory())
                .likeCount(0)
                .build();

        postRepository.save(post);
        return new PostResponseDto(post);
    }

    /**
     * 게시물 조회 (페이지네이션)
     * @param page 요청한 페이지 번호
     * @param size 페이지당 게시물 수
     * @return 페이지네이션된 게시물의 응답 데이터
     */
    public Page<PostResponseDto> getPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> posts = postRepository.findAll(pageable);
        return posts.map(PostResponseDto::new);
    }

    /**
     * 게시물 수정
     * @param postId 수정할 게시물의 ID
     * @param postRequestDto 수정할 게시물의 정보
     * @param user 게시물을 수정하려는 사용자
     * @return 수정된 게시물의 응답 데이터
     */
    public PostResponseDto updatePost(Long postId, PostRequestDto postRequestDto, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(NotFoundPostException::new);

        String role = user.getRole();
        System.out.println("현재 사용자의 역할: " + role);

        if (!post.getUser().getId().equals(user.getId()) && !Role.ADMIN.name().equals(role)) {
            throw new UnauthorizedException("작성자 또는 관리자만 수정할 수 있습니다.");
        }

        if (!postRequestDto.isValidCategory()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "카테고리에 일상공유, 고민상담, 익명토론 중 하나를 입력해주세요.");
        }

        post.setContent(postRequestDto.getContent());
        post.setCategory(postRequestDto.getCategory());

        postRepository.save(post);
        return new PostResponseDto(post);
    }

    /**
     * 게시물 삭제
     * @param postId 삭제할 게시물의 ID
     * @param user 게시물을 삭제하려는 사용자
     */
    @Transactional
    public void deletePost(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(NotFoundPostException::new);

        String role = user.getRole();
        System.out.println("현재 사용자의 역할: " + role); // 디버깅용

        if (!post.getUser().getId().equals(user.getId()) && !Role.ADMIN.name().equals(role)) {
            throw new UnauthorizedException("작성자 또는 관리자만 삭제할 수 있습니다.");
        }

        // 관련된 post_like 레코드 삭제
        postLikeRepository.deleteByPostId(postId);

        postRepository.delete(post);
    }

    /**
     * 게시물 ID로 게시물 찾기
     * @param id 게시물 ID
     * @return 찾은 게시물
     */
    public Post findPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new NotFoundPostException());
    }
}
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


@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;


    /**
     * 게시물을 생성하는 메서드
     * @param postRequestDto 게시물 요청 데이터
     * @param user 게시물 작성자
     * @return 생성된 게시물의 응답 데이터
     */
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
     * 게시물을 페이지네이션하여 조회하는 메서드
     * @param page 조회할 페이지 번호
     * @param size 페이지 당 항목 수(5개로 고정)
     * @return 페이지네이션된 게시물 목록
     */
    public Page<PostResponseDto> getPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> posts = postRepository.findAll(pageable);
        return posts.map(PostResponseDto::new);
    }

    /**
     * 게시물을 수정하는 메서드
     * @param postId 수정할 게시물 ID
     * @param postRequestDto 게시물 요청 데이터
     * @param user 게시물 작성자
     * @return 수정된 게시물의 응답 데이터
     */
    public PostResponseDto updatePost(Long postId, PostRequestDto postRequestDto, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(NotFoundPostException::new);

        if (!post.getUser().getId().equals(user.getId())) {
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
     * 게시물을 삭제하는 메서드
     * @param postId 삭제할 게시물 ID
     * @param user 게시물 작성자
     */
    public void deletePost(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(NotFoundPostException::new);

        if (!post.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("작성자 또는 관리자만 삭제할 수 있습니다.");
        }

        postRepository.delete(post);
    }

    // postId 로 post 객체 받아오는 메소드 / 주문 수정 기능에 필요
    public Post findPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new NotFoundPostException());
    }
}
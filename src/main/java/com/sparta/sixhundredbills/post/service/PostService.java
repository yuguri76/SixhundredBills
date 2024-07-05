package com.sparta.sixhundredbills.post.service;

import com.sparta.sixhundredbills.auth.entity.Role;
import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.comment.entity.Comment;
import com.sparta.sixhundredbills.comment.repository.CommentRepository;
import com.sparta.sixhundredbills.exception.CustomException;
import com.sparta.sixhundredbills.exception.ErrorEnum;
import com.sparta.sixhundredbills.post.dto.PostRequestDto;
import com.sparta.sixhundredbills.post.dto.PostResponseDto;
import com.sparta.sixhundredbills.post.entity.Post;
import com.sparta.sixhundredbills.post.repository.PostRepository;
import com.sparta.sixhundredbills.post_like.repository.PostLikeRepository;
import com.sparta.sixhundredbills.util.AnonymousNameGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 게시물 서비스 클래스
 * 게시물 생성, 조회, 수정, 삭제 등의 기능을 제공
 */
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;

    /**
     * 게시물 생성 메서드
     * @param postRequestDto 게시물 요청 DTO
     * @param user 사용자 엔티티
     * @return 게시물 응답 DTO
     */
    @Transactional
    public PostResponseDto createPost(PostRequestDto postRequestDto, User user) {
        Post post = Post.builder()
                .user(user)
                .category(postRequestDto.getCategory())
                .content(postRequestDto.getContent())
                .showName(AnonymousNameGenerator.generate())
                .likeCount(0)
                .build(); // 새로운 게시물 객체 생성
        postRepository.save(post); // 게시물 저장
        return new PostResponseDto(post, 0); // 응답 DTO 반환
    }

    /**
     * 모든 게시물 페이지네이션 조회 메서드
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 게시물 응답 DTO 페이지
     */
    @Transactional(readOnly = true)
    public Page<PostResponseDto> getPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));  // 페이지네이션 설정
        Page<Post> posts = postRepository.findAll(pageable); // 게시물 조회
        return posts.map(post -> new PostResponseDto(post, postLikeRepository.countByPostId(post.getId()))); // 응답 DTO 반환
    }

    /**
     * 특정 게시물 조회 메서드
     * @param postId 게시물 ID
     * @return 게시물 응답 DTO
     */
    @Transactional(readOnly = true)
    public PostResponseDto getPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new CustomException(ErrorEnum.POST_NOT_FOUND));
        return new PostResponseDto(post, postLikeRepository.countByPostId(postId));
    }

    /**
     * 게시물 ID로 게시물 조회 메서드
     * @param postId 게시물 ID
     * @return 게시물 엔티티
     */
    @Transactional(readOnly = true)
    public Post findPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new CustomException(ErrorEnum.POST_NOT_FOUND));
    }

    /**
     * 게시물 수정 메서드
     * @param postId 게시물 ID
     * @param postRequestDto 게시물 요청 DTO
     * @param user 사용자 엔티티
     * @return 게시물 응답 DTO
     */
    @Transactional
    public PostResponseDto updatePost(Long postId, PostRequestDto postRequestDto, User user) {
        Post post = findPostById(postId);  // 게시물 조회

        // 관리자나 게시물 작성자만 게시물을 수정할 수 있도록 수정
        if (!post.getUser().getId().equals(user.getId()) && !user.getRole().equals(Role.ADMIN.name())) {
            throw new CustomException(ErrorEnum.NOT_ROLE);
        }

        post.update(postRequestDto, user); // 게시물 수정
        postRepository.save(post);  // 게시물 저장
        return new PostResponseDto(post, postLikeRepository.countByPostId(postId)); // 응답 DTO 반환
    }

    /**
     * 게시물 삭제 메서드
     * @param postId 게시물 ID
     * @param user 사용자 엔티티
     */
    @Transactional
    public void deletePost(Long postId, User user) {
        Post post = findPostById(postId);  // 게시물 조회

        // 관리자나 게시물 작성자만 게시물을 삭제할 수 있도록 수정
        if (!post.getUser().getId().equals(user.getId()) && !user.getRole().equals(Role.ADMIN.name())) {
            throw new CustomException(ErrorEnum.NOT_ROLE);
        }

        // 하위 댓글 삭제
        deleteCommentsRecursively(post);

        // 게시물의 좋아요 삭제
        postLikeRepository.deleteByPostId(postId);

        postRepository.delete(post);
    }

    /**
     * 게시물의 하위 댓글 재귀적으로 삭제 메서드
     * @param post 게시물 엔티티
     */
    private void deleteCommentsRecursively(Post post) {
        List<Comment> comments = commentRepository.findAllByPost(post, Pageable.unpaged()).getContent(); // 하위 댓글 조회
        for (Comment comment : comments) {
            deleteCommentsRecursively(comment); // 하위 댓글 재귀 삭제
            commentRepository.delete(comment); // 댓글 삭제
        }
    }

    /**
     * 댓글의 하위 댓글 재귀적으로 삭제 메서드
     * @param comment 댓글 엔티티
     */
    private void deleteCommentsRecursively(Comment comment) {
        List<Comment> childrenComments = commentRepository.findAllByParentCommentId(comment.getId());  // 하위 댓글 조회
        for (Comment child : childrenComments) {
            deleteCommentsRecursively(child); // 하위 댓글 재귀 삭제
        }
    }
}

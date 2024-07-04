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

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public PostResponseDto createPost(PostRequestDto postRequestDto, User user) {
        Post post = Post.builder()
                .user(user)
                .category(postRequestDto.getCategory())
                .content(postRequestDto.getContent())
                .showName(AnonymousNameGenerator.generate())
                .likeCount(0)
                .build();
        postRepository.save(post);
        return new PostResponseDto(post, 0);
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDto> getPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> posts = postRepository.findAll(pageable);
        return posts.map(post -> new PostResponseDto(post, postLikeRepository.countByPostId(post.getId())));
    }

    @Transactional(readOnly = true)
    public PostResponseDto getPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new CustomException(ErrorEnum.POST_NOT_FOUND));
        return new PostResponseDto(post, postLikeRepository.countByPostId(postId));
    }

    @Transactional(readOnly = true)
    public Post findPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new CustomException(ErrorEnum.POST_NOT_FOUND));
    }

    @Transactional
    public PostResponseDto updatePost(Long postId, PostRequestDto postRequestDto, User user) {
        Post post = findPostById(postId);

        // 관리자나 게시물 작성자만 게시물을 수정할 수 있도록 수정
        if (!post.getUser().getId().equals(user.getId()) && !user.getRole().equals(Role.ADMIN.name())) {
            throw new CustomException(ErrorEnum.NOT_ROLE);
        }

        post.update(postRequestDto, user);
        postRepository.save(post);
        return new PostResponseDto(post, postLikeRepository.countByPostId(postId));
    }

    @Transactional
    public void deletePost(Long postId, User user) {
        Post post = findPostById(postId);

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

    private void deleteCommentsRecursively(Post post) {
        List<Comment> comments = commentRepository.findAllByPost(post, Pageable.unpaged()).getContent();
        for (Comment comment : comments) {
            deleteCommentsRecursively(comment);
            commentRepository.delete(comment);
        }
    }

    private void deleteCommentsRecursively(Comment comment) {
        List<Comment> childrenComments = commentRepository.findAllByParentCommentId(comment.getId());
        for (Comment child : childrenComments) {
            deleteCommentsRecursively(child);
        }
    }
}

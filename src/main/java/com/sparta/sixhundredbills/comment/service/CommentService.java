package com.sparta.sixhundredbills.comment.service;

import com.sparta.sixhundredbills.auth.entity.Role;
import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.auth.repository.UserRepository;
import com.sparta.sixhundredbills.auth.security.UserDetailsImpl;
import com.sparta.sixhundredbills.comment.dto.CommentRequestDto;
import com.sparta.sixhundredbills.comment.dto.CommentResponseDto;
import com.sparta.sixhundredbills.comment.entity.Comment;
import com.sparta.sixhundredbills.comment.repository.CommentRepository;
import com.sparta.sixhundredbills.comment_like.repository.CommentLikeRepository;
import com.sparta.sixhundredbills.exception.ErrorEnum;
import com.sparta.sixhundredbills.exception.NotFoundCommentException;
import com.sparta.sixhundredbills.exception.NotFoundPostException;
import com.sparta.sixhundredbills.exception.UnauthorizedException;
import com.sparta.sixhundredbills.post.entity.Post;
import com.sparta.sixhundredbills.post.service.PostService;
import com.sparta.sixhundredbills.util.AnonymousNameGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostService postService;

    public CommentResponseDto createComment(Long postId, Long parentCommentId, CommentRequestDto requestDto, UserDetailsImpl userDetails) {
        Post post = postService.findPostById(postId);
        String showName = AnonymousNameGenerator.generate();
        Comment parentComment = null;
        if (parentCommentId != null) {
            parentComment = commentRepository.findById(parentCommentId)
                    .orElseThrow(() -> new NotFoundCommentException(ErrorEnum.NOT_COMMENT));
        }

        Comment newComment = Comment.builder()
                .post(post)
                .user(userDetails.getUser())
                .showName(showName)
                .comment(requestDto.getComment())
                .build();

        if (parentComment != null) {
            newComment.setParentComment(parentComment);
            parentComment.getChildrenComment().add(newComment);
        }

        commentRepository.save(newComment);
        return CommentResponseDto.builder()
                .comment(newComment.getComment())
                .showName(newComment.getShowName())
                .likesCount(newComment.getLikesCount()) // 좋아요 수 추가
                .build();
    }

    public List<CommentResponseDto> getComments(Long postId, int page, int size, String sortBy) {
        Post post = postService.findPostById(postId);
        Sort sort = Sort.by(Sort.Direction.DESC, sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CommentResponseDto> CommentPage = commentRepository.findAllByPost(post, pageable).map(
                comment -> CommentResponseDto.builder()
                        .comment(comment.getComment())
                        .showName(comment.getShowName())
                        .likesCount(commentLikeRepository.countByComment(comment)) // 좋아요 수 추가
                        .build()
        );

        List<CommentResponseDto> responseDtoList = CommentPage.getContent();

        if (responseDtoList.isEmpty()) {
            throw new NotFoundPostException(ErrorEnum.NOT_POST);
        }
        return responseDtoList;
    }

    public CommentResponseDto updateComment(Long postId, Long commentId, CommentRequestDto requestDto, UserDetailsImpl userDetails) {
        Post post = postService.findPostById(postId);
        Comment comment = findByCommentId(commentId);
        User user = userDetails.getUser();

        if (!comment.getUser().getId().equals(user.getId()) && !user.getRole().equals(Role.ADMIN.name())) {
            throw new UnauthorizedException(ErrorEnum.NOT_ROLE);
        }

        comment.updateComment(requestDto, user, post);

        Comment saveComment = commentRepository.save(comment);

        return CommentResponseDto.builder()
                .comment(saveComment.getComment())
                .showName(saveComment.getShowName())
                .likesCount(saveComment.getLikesCount()) // 좋아요 수 추가
                .build();
    }

    public Comment findByCommentId(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundCommentException(ErrorEnum.NOT_COMMENT));
    }

    @Transactional
    public String deleteComment(Long postId, Long commentId, UserDetailsImpl userDetails) {
        Post post = postService.findPostById(postId);
        Comment comment = findByCommentId(commentId);
        User user = userDetails.getUser();

        if (!comment.getUser().getId().equals(user.getId()) && !user.getRole().equals(Role.ADMIN.name())) {
            throw new UnauthorizedException(ErrorEnum.NOT_ROLE);
        }

        deleteChildComments(comment);

        commentRepository.delete(comment);
        return "댓글이 삭제되었습니다.";
    }

    private void deleteChildComments(Comment parentComment) {
        for (Comment childComment : parentComment.getChildrenComment()) {
            deleteChildComments(childComment);
            commentRepository.delete(childComment);
        }
    }
}

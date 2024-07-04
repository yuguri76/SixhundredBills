package com.sparta.sixhundredbills.comment_like.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.comment.entity.Comment;
import com.sparta.sixhundredbills.comment.repository.CommentRepository;
import com.sparta.sixhundredbills.comment_like.dto.CommentLikeResponseDto;
import com.sparta.sixhundredbills.comment_like.entity.CommentLike;
import com.sparta.sixhundredbills.comment_like.repository.CommentLikeRepository;
import com.sparta.sixhundredbills.exception.CustomException;
import com.sparta.sixhundredbills.exception.ErrorEnum;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;
    private final JPAQueryFactory queryFactory;

    @Transactional
    public CommentLikeResponseDto likeComment(Long postId, Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorEnum.COMMENT_NOT_FOUND));

        if (comment.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorEnum.CANNOT_LIKE_OWN_COMMENT);
        }

        Optional<CommentLike> commentLike = commentLikeRepository.findByUserAndComment(user, comment);
        if (commentLike.isPresent()) {
            throw new CustomException(ErrorEnum.COMMENT_ALREADY_LIKED);
        }

        CommentLike newLike = CommentLike.builder()
                .user(user)
                .comment(comment)
                .build();

        commentLikeRepository.save(newLike);

        return CommentLikeResponseDto.builder()
                .content("성공적으로 좋아요를 등록했습니다")
                .commentId(commentId)
                .userId(user.getId())
                .likesCount(commentLikeRepository.countByCommentId(commentId))
                .build();
    }

    @Transactional
    public CommentLikeResponseDto unlikeComment(Long postId, Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorEnum.COMMENT_NOT_FOUND));

        CommentLike commentLike = commentLikeRepository.findByUserAndComment(user, comment)
                .orElseThrow(() -> new CustomException(ErrorEnum.LIKE_NOT_FOUND));

        commentLikeRepository.delete(commentLike);

        return CommentLikeResponseDto.builder()
                .content("성공적으로 좋아요를 취소했습니다")
                .commentId(commentId)
                .userId(user.getId())
                .likesCount(commentLikeRepository.countByCommentId(commentId))
                .build();
    }

    @Transactional(readOnly = true)
    public Page<CommentLikeResponseDto> getLikedComments(User user, Pageable pageable) {
        Page<CommentLike> likedComments = commentLikeRepository.findAllByUser(user, pageable);
        return likedComments.map(commentLike -> CommentLikeResponseDto.builder()
                .content(commentLike.getComment().getComment())  // 댓글의 내용을 content에 설정
                .commentId(commentLike.getComment().getId())
                .userId(user.getId())
                .likesCount(commentLikeRepository.countByCommentId(commentLike.getComment().getId()))
                .build());
    }
}

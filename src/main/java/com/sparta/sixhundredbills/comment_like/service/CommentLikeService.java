package com.sparta.sixhundredbills.comment_like.service;

import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.comment.entity.Comment;
import com.sparta.sixhundredbills.comment.repository.CommentRepository;
import com.sparta.sixhundredbills.comment_like.dto.CommentLikeResponseDto;
import com.sparta.sixhundredbills.comment_like.entity.CommentLike;
import com.sparta.sixhundredbills.comment_like.repository.CommentLikeRepository;
import com.sparta.sixhundredbills.exception.CustomException;
import com.sparta.sixhundredbills.exception.ErrorEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 댓글 좋아요 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;

    /**
     * 댓글에 좋아요를 추가하는 메서드.
     *
     * @param postId 게시물 ID
     * @param commentId 댓글 ID
     * @param user 사용자 정보
     * @return 좋아요 추가 결과를 포함한 CommentLikeResponseDto
     */
    public CommentLikeResponseDto likeComment(Long postId, Long commentId, User user) {
        // 댓글 존재 여부 확인
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorEnum.COMMENT_NOT_FOUND));

        // 자신이 작성한 댓글에 좋아요를 누를 수 없도록 예외 처리
        if (comment.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorEnum.CANNOT_LIKE_OWN_COMMENT);
        }

        // 이미 좋아요를 눌렀는지 확인
        Optional<CommentLike> commentLike = commentLikeRepository.findByUserAndComment(user, comment);
        if (commentLike.isPresent()) {
            throw new CustomException(ErrorEnum.COMMENT_ALREADY_LIKED);
        }

        // 새로운 좋아요 엔티티 생성 및 저장
        CommentLike newLike = CommentLike.builder()
                .user(user)
                .comment(comment)
                .build();

        commentLikeRepository.save(newLike);

        // 좋아요 추가 결과 반환
        return CommentLikeResponseDto.builder()
                .message("성공적으로 좋아요를 등록했습니다")
                .commentId(commentId)
                .userId(user.getId())
                .likesCount(commentLikeRepository.countByCommentId(commentId))
                .build();
    }

    /**
     * 댓글의 좋아요를 취소하는 메서드.
     *
     * @param postId 게시물 ID
     * @param commentId 댓글 ID
     * @param user 사용자 정보
     * @return 좋아요 취소 결과를 포함한 CommentLikeResponseDto
     */

    public CommentLikeResponseDto unlikeComment(Long postId, Long commentId, User user) {
        // 댓글 존재 여부 확인
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorEnum.COMMENT_NOT_FOUND));

        // 좋아요가 눌린 적이 있는지 확인
        CommentLike commentLike = commentLikeRepository.findByUserAndComment(user, comment)
                .orElseThrow(() -> new CustomException(ErrorEnum.LIKE_NOT_FOUND));

        // 좋아요 엔티티 삭제
        commentLikeRepository.delete(commentLike);

        // 좋아요 취소 결과 반환
        return CommentLikeResponseDto.builder()
                .message("성공적으로 좋아요를 취소했습니다")
                .commentId(commentId)
                .userId(user.getId())
                .likesCount(commentLikeRepository.countByCommentId(commentId))
                .build();
    }
}
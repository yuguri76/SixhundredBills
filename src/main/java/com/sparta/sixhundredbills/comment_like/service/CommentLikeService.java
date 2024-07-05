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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 댓글 좋아요 서비스 클래스
 * 댓글 좋아요 등록, 취소 및 조회 등의 기능을 제공
 */
@Service
@RequiredArgsConstructor
public class CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;
    private final JPAQueryFactory queryFactory;

    /**
     * 댓글 좋아요 등록 메서드
     * @param postId 게시물 ID
     * @param commentId 댓글 ID
     * @param user 사용자 엔티티
     * @return 댓글 좋아요 응답 DTO
     */
    @Transactional
    public CommentLikeResponseDto likeComment(Long postId, Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorEnum.COMMENT_NOT_FOUND)); // 댓글 조회

        if (comment.getUser() == null || comment.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorEnum.CANNOT_LIKE_OWN_COMMENT); // 자기 댓글에 좋아요 금지
        }

        Optional<CommentLike> commentLike = commentLikeRepository.findByUserAndComment(user, comment);
        if (commentLike.isPresent()) {
            throw new CustomException(ErrorEnum.COMMENT_ALREADY_LIKED);  // 이미 좋아요한 댓글 예외 처리
        }

        CommentLike newLike = CommentLike.builder()
                .user(user)
                .comment(comment)
                .build(); // 새로운 댓글 좋아요 객체 생성

        commentLikeRepository.save(newLike); // 댓글 좋아요 저장

        return CommentLikeResponseDto.builder()
                .content("성공적으로 좋아요를 등록했습니다")
                .commentId(commentId)
                .userId(user.getId())
                .likesCount(commentLikeRepository.countByCommentId(commentId))
                .build(); // 응답 DTO 생성
    }

    /**
     * 댓글 좋아요 취소 메서드
     * @param postId 게시물 ID
     * @param commentId 댓글 ID
     * @param user 사용자 엔티티
     * @return 댓글 좋아요 응답 DTO
     */
    @Transactional
    public CommentLikeResponseDto unlikeComment(Long postId, Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorEnum.COMMENT_NOT_FOUND)); // 댓글 조회

        CommentLike commentLike = commentLikeRepository.findByUserAndComment(user, comment)
                .orElseThrow(() -> new CustomException(ErrorEnum.LIKE_NOT_FOUND)); // 댓글 좋아요 조회

        commentLikeRepository.delete(commentLike); // 댓글 좋아요 삭제

        return CommentLikeResponseDto.builder()
                .content("성공적으로 좋아요를 취소했습니다")
                .commentId(commentId)
                .userId(user.getId())
                .likesCount(commentLikeRepository.countByCommentId(commentId)) // 좋아요 수 추가
                .build();  // 응답 DTO 생성
    }

    /**
     * 사용자가 좋아요한 댓글 목록 조회 메서드
     * @param user 사용자 엔티티
     * @param pageable 페이지 정보
     * @return 좋아요한 댓글 목록 응답 DTO 페이지
     */
    @Transactional(readOnly = true)
    public Page<CommentLikeResponseDto> getLikedComments(User user, Pageable pageable) {
        Page<CommentLike> likedComments = commentLikeRepository.findAllByUser(user, pageable); // 좋아요한 댓글 조회
        return likedComments.map(commentLike -> CommentLikeResponseDto.builder()
                .content(commentLike.getComment().getContent())  // 댓글의 내용을 content에 설정
                .commentId(commentLike.getComment().getId())
                .userId(user.getId())
                .likesCount(commentLikeRepository.countByCommentId(commentLike.getComment().getId())) // 좋아요 수 추가
                .build()); // 응답 DTO 생성
    }
}
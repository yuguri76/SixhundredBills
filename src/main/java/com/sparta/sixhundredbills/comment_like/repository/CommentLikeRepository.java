package com.sparta.sixhundredbills.comment_like.repository;

import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.comment.entity.Comment;
import com.sparta.sixhundredbills.comment_like.entity.CommentLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * 댓글 좋아요 레포지토리 인터페이스
 */
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long>, CommentLikeRepositoryCustom {
    /**
     * 사용자와 댓글로 좋아요 찾기
     * @param user 사용자 정보
     * @param comment 댓글 정보
     * @return 좋아요 정보
     */
    Optional<CommentLike> findByUserAndComment(User user, Comment comment);

    /**
     * 특정 댓글의 좋아요 수 조회
     * @param commentId 댓글 ID
     * @return 좋아요 수
     */
    @Query("SELECT COUNT(cl) FROM CommentLike cl WHERE cl.comment.id = :commentId")
    int countByCommentId(Long commentId);

    /**
     * 사용자별 좋아요한 댓글 조회 (페이징 처리)
     * @param user 사용자 정보
     * @param pageable 페이지 정보
     * @return 좋아요한 댓글 목록 (페이징 처리)
     */
    Page<CommentLike> findAllByUser(User user, Pageable pageable);

    // 댓글에 달린 좋아요 수를 세는 메서드 추가
    int countByComment(Comment comment);
}
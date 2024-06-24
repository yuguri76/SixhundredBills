package com.sparta.sixhundredbills.comment_like.repository;

import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.comment.entity.Comment;
import com.sparta.sixhundredbills.comment_like.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * 댓글 좋아요 레포지토리 인터페이스
 */

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    // 사용자와 댓글로 좋아요 찾기
    Optional<CommentLike> findByUserAndComment(User user, Comment comment);

    // 댓글 좋아요 투표 갯수 카운트 쿼리 (좋아요 갯수)
    @Query("SELECT COUNT(cl) FROM CommentLike cl WHERE cl.comment.id = :commentId")
    int countByCommentId(Long commentId);
}
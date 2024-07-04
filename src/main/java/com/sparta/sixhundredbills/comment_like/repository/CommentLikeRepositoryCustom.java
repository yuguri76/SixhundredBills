package com.sparta.sixhundredbills.comment_like.repository;

import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.comment_like.entity.CommentLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentLikeRepositoryCustom {
    /**
     * 사용자별 좋아요한 댓글 개수 조회
     * @param user 사용자 정보
     * @return 좋아요한 댓글 개수
     */
    long countByUser(User user);

    /**
     * 사용자별 좋아요한 댓글 조회 (페이징 처리)
     * @param user 사용자 정보
     * @param pageable 페이지 정보
     * @return 좋아요한 댓글 목록 (페이징 처리)
     */
    Page<CommentLike> findLikedCommentsByUser(User user, Pageable pageable);
}
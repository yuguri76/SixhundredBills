package com.sparta.sixhundredbills.comment.repository;

import com.sparta.sixhundredbills.comment.entity.Comment;
import com.sparta.sixhundredbills.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Comment 엔티티에 대한 데이터 접근 레이어 (Repository).
 * 기본적인 CRUD 작업을 수행하는 메서드와 커스텀 쿼리를 포함.
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {
    /**
     * 특정 게시물에 달린 모든 댓글을 페이지네이션하여 조회
     * @param post 게시물 객체
     * @param pageable 페이지 정보
     * @return 페이지네이션된 댓글 목록
     */
    Page<Comment> findAllByPost(Post post, Pageable pageable);

    /**
     * 특정 게시물에 달린 모든 댓글을 삭제
     * @param postId 게시물 ID
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM Comment c WHERE c.post.id = :postId")
    void deleteByPostId(Long postId);

    /**
     * 특정 부모 댓글에 달린 모든 자식 댓글을 삭제
     * @param parentId 부모 댓글 ID
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM Comment c WHERE c.parentComment.id = :parentId")
    void deleteByParentId(Long parentId);

    /**
     * 특정 부모 댓글에 달린 모든 자식 댓글을 조회
     * @param parentId 부모 댓글 ID
     * @return 자식 댓글 목록
     */
    List<Comment> findAllByParentCommentId(Long parentId);

    /**
     * 특정 게시물에 달린 모든 댓글을 조회
     * @param postId 게시물 ID
     * @return 댓글 목록
     */
    List<Comment> findAllByPostId(Long postId);
}

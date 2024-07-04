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

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByPost(Post post, Pageable pageable);

    @Transactional
    @Modifying
    @Query("DELETE FROM Comment c WHERE c.post.id = :postId")
    void deleteByPostId(Long postId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Comment c WHERE c.parentComment.id = :parentId")
    void deleteByParentId(Long parentId);

    List<Comment> findAllByParentCommentId(Long parentId);

    // 새로 추가된 메서드
    List<Comment> findAllByPostId(Long postId);
}

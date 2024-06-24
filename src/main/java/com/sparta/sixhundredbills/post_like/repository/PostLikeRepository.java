package com.sparta.sixhundredbills.post_like.repository;

import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.post.entity.Post;
import com.sparta.sixhundredbills.post_like.entity.PostLike;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


/**
 * 게시물 좋아요 레포지토리 인터페이스
 */
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    // 사용자와 게시물로 좋아요 찾기
    Optional<PostLike> findByUserAndPost(User user, Post post);

    //postlike 투플 갯수 카운트 쿼리(좋아요 갯수)
    @Query("SELECT COUNT(pl) FROM PostLike pl WHERE pl.post.id = :postId")
    int countByPostId(Long postId);

    @Transactional
    @Modifying
    @Query("DELETE FROM PostLike pl WHERE pl.post.id = :postId")
    void deleteByPostId(Long postId);
}
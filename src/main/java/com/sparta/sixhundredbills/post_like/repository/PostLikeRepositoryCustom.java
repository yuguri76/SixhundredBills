package com.sparta.sixhundredbills.post_like.repository;

import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.post_like.entity.PostLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostLikeRepositoryCustom {
    long countByUser(User user);
    void deleteByPostId(Long postId);
    Page<PostLike> findLikedPostsByUser(User user, Pageable pageable);
}
package com.sparta.sixhundredbills.post.repository;

import com.sparta.sixhundredbills.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * 모든 게시물을 페이지네이션하여 조회
     * @param pageable 페이지 정보
     * @return 페이지네이션된 게시물 목록
     */
    Page<Post> findAll(Pageable pageable); // 모든 게시물을 페이지네이션하여 조회
}
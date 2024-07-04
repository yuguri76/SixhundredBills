package com.sparta.sixhundredbills.post_like.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.post_like.entity.PostLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;

import java.util.List;

import static com.sparta.sixhundredbills.post_like.entity.QPostLike.postLike;

@Repository
public class PostLikeRepositoryCustomImpl implements PostLikeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public PostLikeRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * 사용자별 좋아요한 게시물 개수 조회
     * @param user 사용자 정보
     * @return 좋아요한 게시물 개수
     */
    @Override
    public long countByUser(User user) {
        return queryFactory
                .selectFrom(postLike)
                .where(postLike.user.eq(user))
                .fetchCount();
    }

    /**
     * 특정 게시물의 좋아요 삭제
     * @param postId 게시물 ID
     */
    @Override
    @Transactional
    public void deleteByPostId(Long postId) {
        queryFactory
                .delete(postLike)
                .where(postLike.post.id.eq(postId))
                .execute();
    }

    /**
     * 사용자별 좋아요한 게시물 조회 (페이징 처리)
     * @param user 사용자 정보
     * @param pageable 페이지 정보
     * @return 좋아요한 게시물 목록 (페이징 처리)
     */
    @Override
    public Page<PostLike> findLikedPostsByUser(User user, Pageable pageable) {
        List<PostLike> results = queryFactory
                .selectFrom(postLike)
                .where(postLike.user.eq(user))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(postLike)
                .where(postLike.user.eq(user))
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }
}
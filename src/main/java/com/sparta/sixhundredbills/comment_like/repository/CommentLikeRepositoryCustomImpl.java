package com.sparta.sixhundredbills.comment_like.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.comment_like.entity.CommentLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sparta.sixhundredbills.comment_like.entity.QCommentLike.commentLike;

@Repository
public class CommentLikeRepositoryCustomImpl implements CommentLikeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CommentLikeRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * 사용자별 좋아요한 댓글 개수 조회
     * @param user 사용자 정보
     * @return 좋아요한 댓글 개수
     */
    @Override
    public long countByUser(User user) {
        return queryFactory
                .selectFrom(commentLike)
                .where(commentLike.user.eq(user))
                .fetchCount();
    }

    /**
     * 사용자별 좋아요한 댓글 조회 (페이징 처리)
     * @param user 사용자 정보
     * @param pageable 페이지 정보
     * @return 좋아요한 댓글 목록 (페이징 처리)
     */
    @Override
    public Page<CommentLike> findLikedCommentsByUser(User user, Pageable pageable) {
        List<CommentLike> results = queryFactory
                .selectFrom(commentLike)
                .where(commentLike.user.eq(user))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(commentLike)
                .where(commentLike.user.eq(user))
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }
}
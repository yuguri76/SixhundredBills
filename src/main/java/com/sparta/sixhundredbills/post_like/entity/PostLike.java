package com.sparta.sixhundredbills.post_like.entity;

import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.post.entity.Post;
import com.sparta.sixhundredbills.timestamp.TimeStamp;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 게시물 좋아요 엔티티 클래스
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class PostLike extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Builder
    public PostLike(User user, Post post) {
        this.user = user;
        this.post = post;
    }
}
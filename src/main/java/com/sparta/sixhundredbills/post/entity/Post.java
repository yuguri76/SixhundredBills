package com.sparta.sixhundredbills.post.entity;

import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.timestamp.TimeStamp;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Post extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String category;
    private String content;
    private String showName;

    private int likeCount;

    /**
     * Post 생성자
     * @param user 게시물을 작성한 사용자
     * @param category 게시물 카테고리
     * @param content 게시물 내용
     * @param showName 익명으로 표시될 이름
     * @param likeCount 좋아요 수
     */
    @Builder
    public Post(User user, String category, String content, String showName, int likeCount) {
        this.user = user;
        this.category = category;
        this.content = content;
        this.showName = showName;
        this.likeCount = likeCount;
    }
}
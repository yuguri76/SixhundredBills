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
    private Long id; // 게시물 ID

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // 작성자

    private String showName; // 익명으로 표시될 이름
    private String content; // 게시물 내용
    private String category; // 게시물 카테고리
    private int likeCount; // 좋아요 수

    @Builder
    public Post(User user, String showName, String content, String category, int likeCount) {
        this.user = user;
        this.showName = showName;
        this.content = content;
        this.category = category;
        this.likeCount = likeCount;
    }

    // 게시물 내용 수정
    public void setContent(String content) {
        this.content = content;
    }

    // 작성자 정보 수정
    public void setUser(User user) {
        this.user = user;
    }

    // 작성자 이름 수정
    public void setShowName(String showName) {
        this.showName = showName;
    }

    // 카테고리 수정
    public void setCategory(String category) {
        this.category = category;
    }
}
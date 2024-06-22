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
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String showName;
    private String content;
    private String category;
    private int likeCount;

    @Builder
    public Post(Long id, User user, String showName, String content, String category, int likeCount) {
        this.id = id;
        this.user = user;
        this.showName = showName;
        this.content = content;
        this.category = category;
        this.likeCount = likeCount;
    }
}
package com.sparta.sixhundredbills.post.entity;

import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.post.dto.PostRequestDto;
import com.sparta.sixhundredbills.post_like.entity.PostLike;
import com.sparta.sixhundredbills.timestamp.TimeStamp;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

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

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PostLike> postLikes;

    @Builder
    public Post(User user, String category, String content, String showName, int likeCount) {
        this.user = user;
        this.category = category;
        this.content = content;
        this.showName = showName;
        this.likeCount = likeCount;
    }

    public void update(PostRequestDto postRequestDto, User user) {
        this.user = user;
        this.content = postRequestDto.getContent();
        this.category = postRequestDto.getCategory();
    }
}

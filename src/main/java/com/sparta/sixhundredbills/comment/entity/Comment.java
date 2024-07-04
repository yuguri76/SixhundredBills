package com.sparta.sixhundredbills.comment.entity;

import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.comment.dto.CommentRequestDto;
import com.sparta.sixhundredbills.comment_like.entity.CommentLike;
import com.sparta.sixhundredbills.post.entity.Post;
import com.sparta.sixhundredbills.timestamp.TimeStamp;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Comment extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> childrenComment = new ArrayList<>();

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CommentLike> commentLikes = new ArrayList<>();

    private String showName;
    private String comment;

    @Builder
    public Comment(Post post, User user, String showName, String comment) {
        this.post = post;
        this.user = user;
        this.showName = showName;
        this.comment = comment;
    }

    public void updateComment(CommentRequestDto requestDto, User user, Post post) {
        this.user = user;
        this.post = post;
        this.comment = requestDto.getComment();
    }

    public int getLikesCount() {
        return commentLikes.size();
    }
}

package com.sparta.sixhundredbills.comment.entity;

import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.comment.dto.CommentRequestDto;
import com.sparta.sixhundredbills.post.entity.Post;
import com.sparta.sixhundredbills.timestamp.TimeStamp;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
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

//    @ManyToOne
//    @JoinColumn(name = "parent_id")
//    private Comment parentId;
    private Long parentId;
    private String showName;
    private String comment;

    public Comment(Post post, User user, Long parentId, String showName, String comment) {
        this.post = post;
        this.user = user;
        this.parentId = parentId;
        this.showName = showName;
        this.comment = comment;
    }

    public void updateComment(CommentRequestDto requestDto, User user, Post post) {
        this.user = user;
        this.post = post;
        this.comment = requestDto.getComment();
    }
}

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

/**
 * Comment 엔티티 클래스
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Comment extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 댓글 ID

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post; // 댓글이 달린 게시물

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // 댓글 작성자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parentComment; // 부모 댓글 (대댓글의 경우)

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> childrenComment = new ArrayList<>(); // 자식 댓글 목록 (대댓글)

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CommentLike> commentLikes = new ArrayList<>(); // 댓글 좋아요 목록

    private String showName;  // 익명으로 표시될 이름
    private String comment;  // 댓글 내용

    /**
     * Comment 엔티티 생성자
     * @param post 댓글이 달린 게시물
     * @param user 댓글 작성자
     * @param showName 익명으로 표시될 이름
     * @param comment 댓글 내용
     */
    @Builder
    public Comment(Post post, User user, String showName, String comment) {
        this.post = post;
        this.user = user;
        this.showName = showName;
        this.comment = comment;
    }

    /**
     * 댓글 수정 메서드
     * @param requestDto 댓글 요청 DTO
     * @param user 댓글 작성자
     * @param post 댓글이 달린 게시물
     */
    public void updateComment(CommentRequestDto requestDto, User user, Post post) {
        this.user = user;
        this.post = post;
        this.comment = requestDto.getComment();
    }

    /**
     * 댓글에 대한 좋아요 수를 반환하는 메서드
     * @return 좋아요 수
     */
    public int getLikesCount() {
        return commentLikes.size();
    }

    /**
     * 댓글 내용을 반환하는 메서드
     * @return 댓글 내용
     */
    // content 필드의 getter 메서드 추가
    public String getContent() {
        return comment;
    }
}

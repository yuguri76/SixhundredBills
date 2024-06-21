package com.sparta.sixhundredbills.comment.repository;

import com.sparta.sixhundredbills.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}

package com.sparta.sixhundredbills.comment.service;

import com.sparta.sixhundredbills.auth.repository.UserRepository;
import com.sparta.sixhundredbills.auth.security.UserDetailsImpl;
import com.sparta.sixhundredbills.comment.dto.CommentRequestDto;
import com.sparta.sixhundredbills.comment.dto.CommentResponseDto;
import com.sparta.sixhundredbills.comment.entity.Comment;
import com.sparta.sixhundredbills.comment.repository.CommentRepository;
import com.sparta.sixhundredbills.exception.NotFoundPostException;
import com.sparta.sixhundredbills.post.entity.Post;
import com.sparta.sixhundredbills.post.repository.PostRepository;
import com.sparta.sixhundredbills.util.AnonymousNameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    @Autowired
    CommentRepository commentRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    UserRepository userRepository;

    public CommentResponseDto createComment(Long postId, CommentRequestDto requestDto, UserDetailsImpl userDetails) {
        // postId로 게시물 찾기 -> 그 게시물이 없을 경우 예외처리
        Post post = postRepository.findById(postId).orElseThrow(NotFoundPostException::new);

        // 익명 닉네임 생성
        String showName = AnonymousNameGenerator.generate();

        // 댓글 Entity 를 DB에 저장하기
        Comment comment = new Comment(post, userDetails.getUser(), 9999L, showName, requestDto.getComment());
        commentRepository.save(comment);

        // CommentResponseDto 를 반환하기
        return new CommentResponseDto(comment);
    }
}

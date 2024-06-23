package com.sparta.sixhundredbills.comment.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.auth.repository.UserRepository;
import com.sparta.sixhundredbills.auth.security.UserDetailsImpl;
import com.sparta.sixhundredbills.comment.dto.CommentRequestDto;
import com.sparta.sixhundredbills.comment.dto.CommentResponseDto;
import com.sparta.sixhundredbills.comment.entity.Comment;
import com.sparta.sixhundredbills.comment.repository.CommentRepository;
import com.sparta.sixhundredbills.exception.NotFoundPostException;
import com.sparta.sixhundredbills.exception.UnauthorizedException;
import com.sparta.sixhundredbills.post.entity.Post;
import com.sparta.sixhundredbills.post.repository.PostRepository;
import com.sparta.sixhundredbills.post.service.PostService;
import com.sparta.sixhundredbills.util.AnonymousNameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    CommentRepository commentRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired

    private PostService postService;

    // 댓글 생성
    public CommentResponseDto createComment(Long postId, Long parentCommentId, CommentRequestDto requestDto, UserDetailsImpl userDetails) {
        // postId로 게시물 찾기 -> 그 게시물이 없을 경우 예외처리
        Post post = postRepository.findById(postId).orElseThrow(NotFoundPostException::new);
        // 익명 닉네임 생성
        String showName = AnonymousNameGenerator.generate();
        Comment parentComment = null;
        if (parentCommentId != null) {
            parentComment = commentRepository.findById(parentCommentId)
                    .orElseThrow(() -> new NotFoundException("상위 댓글을 찾을 수 없습니다."));
        }

        // 댓글 Entity 를 DB에 저장하기
        Comment newComment = Comment.builder()
                .post(post)
                .user(userDetails.getUser())
                .showName(showName)
                .comment(requestDto.getComment())
                .build();

        if (parentComment != null) {
            newComment.setParentComment(parentComment);
            parentComment.getChildrenComment().add(newComment);
        }

        commentRepository.save(newComment);
        // CommentResponseDto 를 반환하기
        return CommentResponseDto.builder()
                .comment(newComment.getComment())
                .showName(newComment.getShowName())
                .build();
    }

    // 게시물별 댓글 조회
    public List<CommentResponseDto> getComments(Long postId, int page, int size, String sortBy) {
        Post post = postService.findPostById(postId);
        Sort sort = Sort.by(Sort.Direction.DESC, sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CommentResponseDto> CommentPage = commentRepository.findAllByPost(post, pageable).map(
                // List<Comment> [comment1, comment2, comment3]
                comment -> CommentResponseDto.builder()
                        .comment(comment.getComment())
                        .showName(comment.getShowName())
                        .build()
                // CommentResponseDto::new
                // new CommentResponseDto(Comment)
                // Comment -> CommentResponseDto
        );
        List<CommentResponseDto> responseDtoList = CommentPage.getContent();

        if (responseDtoList.isEmpty()) {
            throw new NotFoundPostException();
        }
        return responseDtoList;
    }

    // 댓글 수정
    public CommentResponseDto updateComment(Long postId, Long commentId, CommentRequestDto requestDto, UserDetailsImpl userDetails) {
        Post post = postService.findPostById(postId);
        Comment comment = findByCommentId(commentId);
        User user = userDetails.getUser();

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("작성자만 수정할 수 있습니다.");
        }

        comment.updateComment(requestDto, user, post);

        Comment saveComment = commentRepository.save(comment);

        return CommentResponseDto.builder()
                .comment(saveComment.getComment())
                .showName(saveComment.getShowName())
                .build();
    }

    public Comment findByCommentId(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("댓글을 찾지 못했습니다."));
    }

    // 댓글 삭제
    public String deleteComment(Long postId, Long commentId, UserDetailsImpl userDetails) {
        Post post = postService.findPostById(postId);
        Comment comment = findByCommentId(commentId);
        User user = userDetails.getUser();

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("작성자만 삭제할 수 있습니다.");
        }

        commentRepository.delete(comment);
        return "댓글이 삭제되었습니다.";
    }
}
// 루시드
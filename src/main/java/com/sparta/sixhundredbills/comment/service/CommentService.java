package com.sparta.sixhundredbills.comment.service;

import com.sparta.sixhundredbills.auth.entity.Role;
import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.auth.repository.UserRepository;
import com.sparta.sixhundredbills.auth.security.UserDetailsImpl;
import com.sparta.sixhundredbills.comment.dto.CommentRequestDto;
import com.sparta.sixhundredbills.comment.dto.CommentResponseDto;
import com.sparta.sixhundredbills.comment.entity.Comment;
import com.sparta.sixhundredbills.comment.repository.CommentRepository;
import com.sparta.sixhundredbills.exception.ErrorEnum;
import com.sparta.sixhundredbills.exception.NotFoundCommentException;
import com.sparta.sixhundredbills.exception.NotFoundPostException;
import com.sparta.sixhundredbills.exception.UnauthorizedException;
import com.sparta.sixhundredbills.post.entity.Post;
import com.sparta.sixhundredbills.post.service.PostService;
import com.sparta.sixhundredbills.util.AnonymousNameGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostService postService;

    /**
     * 댓글 생성
     * @param postId 게시물 ID
     * @param parentCommentId 부모 댓글 ID
     * @param requestDto 생성할 댓글 정보
     * @param userDetails 인증된 사용자 정보
     * @return 생성된 댓글의 응답 데이터
     */
    // 댓글 생성
    public CommentResponseDto createComment(Long postId, Long parentCommentId, CommentRequestDto requestDto, UserDetailsImpl userDetails) {
        // postId로 게시물 찾기 -> 그 게시물이 없을 경우 예외처리
        Post post = postService.findPostById(postId);
        // 익명 닉네임 생성
        String showName = AnonymousNameGenerator.generate();
        Comment parentComment = null;
        if (parentCommentId != null) {
            parentComment = commentRepository.findById(parentCommentId)
                    .orElseThrow(() -> new NotFoundCommentException(ErrorEnum.NOT_COMMENT));
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

    /**
     * 게시물별 댓글 조회
     * @param postId 게시물 ID
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param sortBy 정렬 기준
     * @return 조회된 댓글의 응답 데이터 리스트
     */
    public List<CommentResponseDto> getComments(Long postId, int page, int size, String sortBy) {
        Post post = postService.findPostById(postId);
        Sort sort = Sort.by(Sort.Direction.DESC, sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CommentResponseDto> CommentPage = commentRepository.findAllByPost(post, pageable).map(
                comment -> CommentResponseDto.builder()
                        .comment(comment.getComment())
                        .showName(comment.getShowName())
                        .build()
        );
        List<CommentResponseDto> responseDtoList = CommentPage.getContent();

        if (responseDtoList.isEmpty()) {
            throw new NotFoundPostException(ErrorEnum.NOT_POST);
        }
        return responseDtoList;
    }

    /**
     * 댓글 수정
     * @param postId 게시물 ID
     * @param commentId 댓글 ID
     * @param requestDto 수정할 댓글 정보
     * @param userDetails 인증된 사용자 정보
     * @return 수정된 댓글의 응답 데이터
     */
    // 댓글 수정
    public CommentResponseDto updateComment(Long postId, Long commentId, CommentRequestDto requestDto, UserDetailsImpl userDetails) {
        Post post = postService.findPostById(postId);
        Comment comment = findByCommentId(commentId);
        User user = userDetails.getUser();

        if (!comment.getUser().getId().equals(user.getId()) && !user.getRole().equals(Role.ADMIN.name())) {
            throw new UnauthorizedException(ErrorEnum.NOT_ROLE);
        }

        comment.updateComment(requestDto, user, post);

        Comment saveComment = commentRepository.save(comment);

        return CommentResponseDto.builder()
                .comment(saveComment.getComment())
                .showName(saveComment.getShowName())
                .build();
    }

    /**
     * 댓글 ID로 댓글 찾기
     * @param commentId 댓글 ID
     * @return 댓글 엔티티
     */
    public Comment findByCommentId(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundCommentException(ErrorEnum.NOT_COMMENT));
    }

    /**
     * 댓글 삭제
     * @param postId 게시물 ID
     * @param commentId 댓글 ID
     * @param userDetails 인증된 사용자 정보
     * @return 삭제된 댓글 메시지
     */
    @Transactional
    public String deleteComment(Long postId, Long commentId, UserDetailsImpl userDetails) {
        Post post = postService.findPostById(postId);
        Comment comment = findByCommentId(commentId);
        User user = userDetails.getUser();

        if (!comment.getUser().getId().equals(user.getId()) && !user.getRole().equals(Role.ADMIN.name())) {
            throw new UnauthorizedException(ErrorEnum.NOT_ROLE);
        }

        // 하위 댓글 삭제
        deleteChildComments(comment);

        commentRepository.delete(comment);
        return "댓글이 삭제되었습니다.";
    }

    /**
     * 하위 댓글 삭제
     * @param parentComment 부모 댓글
     */
    private void deleteChildComments(Comment parentComment) {
        for (Comment childComment : parentComment.getChildrenComment()) {
            deleteChildComments(childComment);
            commentRepository.delete(childComment);
        }
    }
}
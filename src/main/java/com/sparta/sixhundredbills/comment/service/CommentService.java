package com.sparta.sixhundredbills.comment.service;

import com.sparta.sixhundredbills.auth.entity.Role;
import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.auth.security.UserDetailsImpl;
import com.sparta.sixhundredbills.comment.dto.CommentRequestDto;
import com.sparta.sixhundredbills.comment.dto.CommentResponseDto;
import com.sparta.sixhundredbills.comment.entity.Comment;
import com.sparta.sixhundredbills.comment.repository.CommentRepository;
import com.sparta.sixhundredbills.comment_like.repository.CommentLikeRepository;
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

/**
 * 댓글 서비스 클래스
 * 댓글 생성, 조회, 수정, 삭제 등의 기능을 제공
 */
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostService postService;

    /**
     * 댓글 생성 메서드
     * @param postId 게시물 ID
     * @param parentCommentId 부모 댓글 ID (대댓글일 경우)
     * @param requestDto 댓글 요청 DTO
     * @param userDetails 사용자 정보
     * @return 생성된 댓글 응답 DTO
     */
    public CommentResponseDto createComment(Long postId, Long parentCommentId, CommentRequestDto requestDto, UserDetailsImpl userDetails) {
        Post post = postService.findPostById(postId); // 게시물 조회
        String showName = AnonymousNameGenerator.generate(); // 익명 이름 생성
        Comment parentComment = null;
        if (parentCommentId != null) {
            parentComment = commentRepository.findById(parentCommentId)
                    .orElseThrow(() -> new NotFoundCommentException(ErrorEnum.NOT_COMMENT));
        }

        Comment newComment = Comment.builder()
                .post(post)
                .user(userDetails.getUser())
                .showName(showName)
                .comment(requestDto.getComment())
                .build(); // 새로운 댓글 생성

        if (parentComment != null) {
            newComment.setParentComment(parentComment);
            parentComment.getChildrenComment().add(newComment);  // 부모 댓글에 자식 댓글 추가
        }

        commentRepository.save(newComment); // 댓글 저장
        return CommentResponseDto.builder()
                .comment(newComment.getComment())
                .showName(newComment.getShowName())
                .likesCount(newComment.getLikesCount()) // 좋아요 수 추가
                .build(); // 응답 DTO 생성
    }

    /**
     * 댓글 목록 조회 메서드
     * @param postId 게시물 ID
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param sortBy 정렬 기준
     * @return 댓글 응답 DTO 목록
     */
    public List<CommentResponseDto> getComments(Long postId, int page, int size, String sortBy) {
        Post post = postService.findPostById(postId); // 게시물 조회
        Sort sort = Sort.by(Sort.Direction.DESC, sortBy);

        Pageable pageable = PageRequest.of(page, size, sort); // 페이지 정보 생성
        Page<CommentResponseDto> CommentPage = commentRepository.findAllByPost(post, pageable).map(
                comment -> CommentResponseDto.builder()
                        .comment(comment.getComment())
                        .showName(comment.getShowName())
                        .likesCount(commentLikeRepository.countByComment(comment)) // 좋아요 수 추가
                        .build()
        );

        List<CommentResponseDto> responseDtoList = CommentPage.getContent();

        if (responseDtoList.isEmpty()) {
            throw new NotFoundPostException(ErrorEnum.NOT_POST); // 댓글이 없는 경우 예외 발생
        }
        return responseDtoList; // 응답 DTO 목록 반환
    }

    /**
     * 댓글 수정 메서드
     * @param postId 게시물 ID
     * @param commentId 댓글 ID
     * @param requestDto 댓글 요청 DTO
     * @param userDetails 사용자 정보
     * @return 수정된 댓글 응답 DTO
     */
    public CommentResponseDto updateComment(Long postId, Long commentId, CommentRequestDto requestDto, UserDetailsImpl userDetails) {
        Post post = postService.findPostById(postId); // 게시물 조회
        Comment comment = findByCommentId(commentId); // 댓글 조회
        User user = userDetails.getUser();

        if (!comment.getUser().getId().equals(user.getId()) && !user.getRole().equals(Role.ADMIN.name())) {
            throw new UnauthorizedException(ErrorEnum.NOT_ROLE); // 권한 확인
        }

        comment.updateComment(requestDto, user, post); // 댓글 수정

        Comment saveComment = commentRepository.save(comment); // 수정된 댓글 저장

        return CommentResponseDto.builder()
                .comment(saveComment.getComment())
                .showName(saveComment.getShowName())
                .likesCount(saveComment.getLikesCount()) // 좋아요 수 추가
                .build(); // 응답 DTO 생성
    }

    /**
     * 댓글 ID로 댓글 조회 메서드
     * @param commentId 댓글 ID
     * @return 댓글 엔티티
     */
    public Comment findByCommentId(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundCommentException(ErrorEnum.NOT_COMMENT)); // 댓글 조회
    }

    /**
     * 댓글 삭제 메서드
     * @param postId 게시물 ID
     * @param commentId 댓글 ID
     * @param userDetails 사용자 정보
     * @return 삭제 결과 메시지
     */
    @Transactional
    public String deleteComment(Long postId, Long commentId, UserDetailsImpl userDetails) {
        Post post = postService.findPostById(postId); // 게시물 조회
        Comment comment = findByCommentId(commentId); // 댓글 조회
        User user = userDetails.getUser();

        if (!comment.getUser().getId().equals(user.getId()) && !user.getRole().equals(Role.ADMIN.name())) {
            throw new UnauthorizedException(ErrorEnum.NOT_ROLE); // 권한 확인
        }

        deleteChildComments(comment); // 자식 댓글 삭제

        commentRepository.delete(comment); // 댓글 삭제
        return "댓글이 삭제되었습니다."; // 삭제 결과 메시지 반환
    }

    /**
     * 자식 댓글 삭제 메서드
     * @param parentComment 부모 댓글 엔티티
     */
    private void deleteChildComments(Comment parentComment) {
        for (Comment childComment : parentComment.getChildrenComment()) {
            deleteChildComments(childComment);
            commentRepository.delete(childComment);  // 자식 댓글 삭제
        }
    }
}

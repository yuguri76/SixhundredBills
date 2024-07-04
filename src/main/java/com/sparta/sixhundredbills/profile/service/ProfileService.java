package com.sparta.sixhundredbills.profile.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.auth.repository.UserRepository;
import com.sparta.sixhundredbills.exception.ErrorEnum;
import com.sparta.sixhundredbills.exception.InvalidEnteredException;
import com.sparta.sixhundredbills.profile.dto.ProfileRequestDto;
import com.sparta.sixhundredbills.profile.dto.ProfileResponseDto;
import com.sparta.sixhundredbills.profile.entity.PasswordList;
import com.sparta.sixhundredbills.profile.repository.PasswordListRepository;
import com.sparta.sixhundredbills.post_like.repository.PostLikeRepository;
import com.sparta.sixhundredbills.comment_like.repository.CommentLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final PasswordListRepository passwordListRepository;
    private final PasswordEncoder passwordEncoder;
    private final PostLikeRepository postLikeRepository;
    private final CommentLikeRepository commentLikeRepository;

    private static final int LIST_MAX_SIZE = 3;

    /**
     * 사용자 프로필 조회
     * @param user 현재 인증된 사용자
     * @return 사용자 프로필 응답 데이터
     */
    public ProfileResponseDto getProfile(User user) {
        long likedPostsCount = postLikeRepository.countByUser(user);
        long likedCommentsCount = commentLikeRepository.countByUser(user);

        return ProfileResponseDto.fromUser(user, likedPostsCount, likedCommentsCount);
    }

    /**
     * 사용자 프로필 업데이트
     * @param user 현재 인증된 사용자
     * @param profileRequestDto 프로필 업데이트 정보
     * @return 업데이트된 사용자 프로필 응답 데이터
     */
    @Transactional
    public ProfileResponseDto updateProfile(User user, ProfileRequestDto profileRequestDto) {
        // 이메일로 사용자 조회
        User getUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("사용자가 존재하지 않습니다: "));

        // 현재 비밀번호가 일치하는지 확인
        if (!passwordEncoder.matches(profileRequestDto.getPassword(), getUser.getPassword())) {
            throw new InvalidEnteredException(ErrorEnum.BAD_PASSWORD);
        }

        // 이전에 사용한 비밀번호인지 확인
        List<PasswordList> usePasswords = passwordListRepository.findByUserOrderByCreatedAtDesc(getUser);
        for (PasswordList passwordList : usePasswords) {
            if (passwordEncoder.matches(profileRequestDto.getNewPassword(), passwordList.getPassword())) {
                throw new InvalidEnteredException(ErrorEnum.BAD_PASSWORD_LIST);
            }
        }

        // 비밀번호 리스트가 최대 크기보다 큰 경우, 가장 오래된 비밀번호 삭제
        if (usePasswords.size() >= LIST_MAX_SIZE) {
            PasswordList passwordList = usePasswords.get(usePasswords.size() - 1);
            passwordListRepository.delete(passwordList);
        }

        // 새로운 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(profileRequestDto.getNewPassword());

        // 새로운 비밀번호 리스트에 추가
        PasswordList newPasswordList = PasswordList.builder()
                .password(encodedPassword)
                .user(getUser)
                .build();

        passwordListRepository.save(newPasswordList);

        // 사용자 프로필 업데이트
        getUser.updateProfile(profileRequestDto, encodedPassword);

        long likedPostsCount = postLikeRepository.countByUser(user);
        long likedCommentsCount = commentLikeRepository.countByUser(user);

        return ProfileResponseDto.fromUser(getUser, likedPostsCount, likedCommentsCount);
    }
}

package com.sparta.sixhundredbills.profile.service;

import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.auth.repository.UserRepository;
import com.sparta.sixhundredbills.exception.InvalidEnteredException;
import com.sparta.sixhundredbills.exception.UnauthorizedException;
import com.sparta.sixhundredbills.profile.dto.ProfileRequestDto;
import com.sparta.sixhundredbills.profile.dto.ProfileResponseDto;
import com.sparta.sixhundredbills.profile.entity.PasswordList;
import com.sparta.sixhundredbills.profile.repository.PasswordListRepository;
import lombok.RequiredArgsConstructor;
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

    // 각 사용자가 가질 수 있는 패스워드 리스트 최대 사이즈
    private static final int LIST_MAX_SIZE = 3;

    /**
     * 프로필 조회하는 메서드
     * @param user 유저 정보
     * @return 요청한 유저의 프로필 정보
     * */
    public ProfileResponseDto getProfile(User user) {
        // 인증된 유저 정보를 프로필 Dto 에 필요한 내용만 담아서 반환
        return ProfileResponseDto.fromUser(user);
    }

    /**
     * 프로필 수정하는 메서드
     * @param profileRequestDto 프로필 수정 요청 DTO
     * @param user 유저 정보
     * @return 수정된 유저의 프로필 정보
     * */
    @Transactional
    public ProfileResponseDto updateProfile(User user, ProfileRequestDto profileRequestDto) {

        // 현재 비밀번호와 입력받은 비밀번호가 동일한지 확인
        if (!passwordEncoder.matches(profileRequestDto.getPassword(), user.getPassword())) {
            throw new InvalidEnteredException("현재 비밀번호와 일치하지 않습니다.");
        }

        // 해당 유저가 사용했던 패스워드 목록 가져오기 (최신순)
        List<PasswordList> usePasswords = passwordListRepository.findByUserOrderByCreatedAtDesc(user);
        // 입력받은 새 비밀번호가 최근 사용했던 패스워드 목록에 존재하는지 확인
        for (PasswordList passwordList : usePasswords) {
            if (passwordEncoder.matches(profileRequestDto.getNewPassword(), passwordList.getPassword())) {
                throw new InvalidEnteredException("새로운 비밀번호는 현재 비밀번호 및 최근 사용한 비밀번호와 달라야 합니다.");
            }
        }

        // 해당 유저의 목록이 3개일 경우 가장 오래된 기록을 삭제
        if (usePasswords.size() >= LIST_MAX_SIZE) {
            PasswordList passwordList = usePasswords.get(usePasswords.size() - 1);
            passwordListRepository.delete(passwordList);
        }

        // 인코딩된 새 패스워드 변수에 저장
        String encodedPassword = passwordEncoder.encode(profileRequestDto.getNewPassword());

        // 3개 미만일 경우 패스워드 목록에 필요한 데이터 설정
        PasswordList newPasswordList = PasswordList.builder()
                .password(encodedPassword)
                .user(user)
                .build();

        // 패스워드 목록에 추가
        passwordListRepository.save(newPasswordList);

        // 수정 요청한 유저 정보 업데이트
        user.updateProfile(profileRequestDto, encodedPassword);

        // 수정 완료된 유저 정보를 프로필 Dto 에 내용을 담아서 반환
        return ProfileResponseDto.fromUser(user);
    }

}

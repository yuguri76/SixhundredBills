package com.sparta.sixhundredbills.auth.service;

import com.sparta.sixhundredbills.auth.dto.SignupRequestDto;
import com.sparta.sixhundredbills.auth.dto.SignupResponseDto;
import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.auth.entity.UserStatusEnum;
import com.sparta.sixhundredbills.auth.exception.CustomException;
import com.sparta.sixhundredbills.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.sparta.sixhundredbills.auth.exception.ErrorEnum.BAD_DUPLICATE;

// 회원 가입 로직을 담당하는 클래스.

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원 가입 로직을 처리하는 메서드
    public SignupResponseDto signup(SignupRequestDto requestDto) {
        // 요청에서 필요한 데이터 추출
        String username = requestDto.getUsername(); // 사용자명
        String password = passwordEncoder.encode(requestDto.getPassword()); // 비밀번호를 암호화하여 저장
        String name = requestDto.getName(); // 사용자 이름
        String intro = requestDto.getIntro(); // 사용자 소개
        UserStatusEnum userStatusEnum = UserStatusEnum.USER_NORMAL; // 사용자 상태 초기화

        // 회원 중복 확인
        Optional<User> checkUsername = userRepository.findByUsername(username);
        if (checkUsername.isPresent()) {
            throw new CustomException(BAD_DUPLICATE); // 이미 존재하는 사용자명일 경우 예외 처리
        }

        String email = requestDto.getEmail(); // 사용자 이메일

        // 새로운 사용자 등록
        User user = new User(username, password, name, email, intro, userStatusEnum); // 새 사용자 객체 생성
        userRepository.save(user); // 사용자 정보 저장

        return new SignupResponseDto(user); // 회원 가입 성공을 나타내는 응답 DTO 생성하여 반환
    }
}

// 회원탈퇴 메서드 ( 필수 구현 조건에 없어서 주석처리 )
//    @Transactional
//    public void resign(User user, ResignDto resignDto) {
//        User userRep = userRepository.findByUsername(user.getUsername()).orElseThrow();
//        if (!passwordEncoder.matches(resignDto.getPassword(), userRep.getPassword())) {
//            throw new CustomException(BAD_PASSWORD);
//        }
//        if (userRep.getUserStatus().equals(UserStatusEnum.USER_RESIGN)) {
//            throw new CustomException(BAD_RESIGN);
//        }
//
//        userRep.resignStatus();
//    }

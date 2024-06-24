package com.sparta.sixhundredbills.auth.service;

import com.sparta.sixhundredbills.auth.dto.SignupRequestDto;
import com.sparta.sixhundredbills.auth.dto.SignupResponseDto;
import com.sparta.sixhundredbills.auth.entity.Role;
import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.auth.entity.UserStatusEnum;
import com.sparta.sixhundredbills.auth.repository.UserRepository;
import com.sparta.sixhundredbills.exception.InvalidEnteredException;
import com.sparta.sixhundredbills.profile.entity.PasswordList;
import com.sparta.sixhundredbills.profile.repository.PasswordListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.sparta.sixhundredbills.exception.ErrorEnum.BAD_DUPLICATE;


// 회원 가입 로직을 담당하는 클래스.

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordListRepository passwordListRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원 가입 로직을 처리하는 메서드
    public SignupResponseDto signup(SignupRequestDto requestDto) {
        // 요청에서 필요한 데이터 추출
        String email = requestDto.getEmail(); // 사용자 이메일
        String password = passwordEncoder.encode(requestDto.getPassword()); // 비밀번호를 암호화하여 저장
        String name = requestDto.getName(); // 사용자 이름
        UserStatusEnum userStatusEnum = UserStatusEnum.USER_NORMAL; // 사용자 상태 초기화

        Role role = requestDto.getRole(); // 사용자가 선택한 권한 설정

        Optional<User> checkUsername = userRepository.findByEmail(email);
        if (checkUsername.isPresent()) {
            throw new InvalidEnteredException(BAD_DUPLICATE); // 이미 존재하는 사용자명일 경우 예외 처리
        }

        // 새로운 사용자 등록
        User user = new User(email, password, name, userStatusEnum, role); // 새 사용자 객체 생성
        userRepository.save(user); // 사용자 정보 저장

        // 패스워드 리스트 추가
        PasswordList newPasswordList = PasswordList.builder()
                .password(password)
                .user(user)
                .build();
        passwordListRepository.save(newPasswordList);

        return new SignupResponseDto(user); // 회원 가입 성공을 나타내는 응답 DTO 생성하여 반환
    }
}

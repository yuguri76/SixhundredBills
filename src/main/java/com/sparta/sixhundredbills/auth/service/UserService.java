package com.sparta.sixhundredbills.auth.service;

import com.sparta.sixhundredbills.auth.dto.SignupRequestDto;
import com.sparta.sixhundredbills.auth.dto.SignupResponseDto;
import com.sparta.sixhundredbills.auth.entity.Role;
import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.auth.entity.UserStatusEnum;
import com.sparta.sixhundredbills.auth.repository.UserRepository;
import com.sparta.sixhundredbills.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.sparta.sixhundredbills.exception.ErrorEnum.BAD_DUPLICATE;


// 회원 가입 로직을 담당하는 클래스.

@Service
@RequiredArgsConstructor
public class UserService {

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
            throw new CustomException(BAD_DUPLICATE); // 이미 존재하는 사용자명일 경우 예외 처리
        }

        // 새로운 사용자 등록
        User user = new User(email, password, name, userStatusEnum, role); // 새 사용자 객체 생성
        userRepository.save(user); // 사용자 정보 저장

        return new SignupResponseDto(user); // 회원 가입 성공을 나타내는 응답 DTO 생성하여 반환
    }


//    // 추후 필요성이 있을 시 사용 할 임시 구현 메서드(필요성 없다고 판단시 삭제 예정)
//    // 사용자의 이메일을 기반으로 저장된 사용자의 역할을 가져오는 메서드
//    // => 이메일로 사용자를 찾고, 그 사용자의 역할을 반환
//    // 사용자 역할(role)을 반환하는 메서드
//    public String getUserRole(String email) {
//        Optional<User> optionalUser = userRepository.findByEmail(email);
//        if (optionalUser.isPresent()) {
//            return optionalUser.get().getRole();
//        }
//        throw new CustomException(USER_NOT_FOUND); // 사용자를 찾지 못한 경우 예외 처리
//    }
}

package com.sparta.sixhundredbills.profile.controller;

import com.sparta.sixhundredbills.auth.security.UserDetailsImpl;
import com.sparta.sixhundredbills.profile.dto.ProfileRequestDto;
import com.sparta.sixhundredbills.profile.dto.ProfileResponseDto;
import com.sparta.sixhundredbills.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/profile")
public class ProfileController {

    private final ProfileService profileService;

    /**
     * 유저의 프로필 조회
     * @param userDetails 인증된 유저 정보
     * @return 유저 프로필 정보
     * */
    @GetMapping
    public ResponseEntity<ProfileResponseDto> getProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        ProfileResponseDto responseDto = profileService.getProfile(userDetails.getUser());
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * 유저 프로필 수정
     * @param profileRequestDto 프로필 수정 요청 DTO
     * @param userDetails 인증된 유저 정보
     * @return 수정된 유저 프로필 정보
     * */
    @PutMapping
    public ResponseEntity<ProfileResponseDto> updateProfile(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody ProfileRequestDto profileRequestDto) {
        ProfileResponseDto responseDto = profileService.updateProfile(userDetails.getUser(), profileRequestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}

package com.even.zaro.controller;

import com.even.zaro.dto.jwt.JwtUserInfoDto;
import com.even.zaro.dto.user.*;
import com.even.zaro.global.ApiResponse;
import com.even.zaro.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;

@Tag(name = "Users", description = "사용자 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @Operation(summary = "내 정보 조회", description = "AccessToken 기반으로 로그인한 사용자의 정보를 조회합니다.", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<UserInfoResponseDto>> getMyInfo(@AuthenticationPrincipal JwtUserInfoDto userInfoDto) {
        UserInfoResponseDto responseDto = userService.getMyInfo(userInfoDto.getUserId());
        return ResponseEntity.ok(ApiResponse.success("내 정보 조회에 성공했습니다.", responseDto));
    }

    @Operation(summary = "프로필 이미지 변경", description = "로그인한 사용자의 프로필 이미지를 변경합니다.", security = {@SecurityRequirement(name = "bearer-key")})
    @PatchMapping("/me/profileImage")
    public ResponseEntity<ApiResponse<UpdateProfileImageResponseDto>> updateProfileImage(
            @RequestBody UpdateProfileImageRequestDto requestDto,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto
    ) {
        UpdateProfileImageResponseDto responseDto = userService.updateProfileImage(userInfoDto.getUserId(), requestDto);
        return ResponseEntity.ok(ApiResponse.success("프로필 이미지가 변경되었습니다.", responseDto));
    }

    @Operation(summary = "닉네임 변경", description = "로그인한 사용자의 닉네임을 변경합니다.", security = {@SecurityRequirement(name = "bearer-key")})
    @PatchMapping("/me/nickname")
    public ResponseEntity<ApiResponse<UpdateNicknameResponseDto>> updateNickname(
            @RequestBody @Valid UpdateNicknameRequestDto requestDto,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto
            ) {
        UpdateNicknameResponseDto responseDto = userService.updateNickname(userInfoDto.getUserId(), requestDto);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        String formattedDate = responseDto.getNextAvailableChangeDate().format(formatter);

        String message = String.format("닉네임이 변경되었습니다. 다음 변경 가능일은 %s 입니다.", formattedDate);
        return ResponseEntity.ok(ApiResponse.success(message, responseDto));
    }

    @Operation(summary = "프로필 변경", description = "로그인한 사용자의 프로필(생일, 자취 시작일, 성별, mbti를 변경합니다.", security = {@SecurityRequirement(name = "bearer-key")})
    @PatchMapping("/me/profile")
    public ResponseEntity<ApiResponse<UpdateProfileResponseDto>> updateProfile(
            @RequestBody UpdateProfileRequestDto requestDto,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto
    ) {
        UpdateProfileResponseDto responseDto = userService.updateProfile(userInfoDto.getUserId(), requestDto);
        return ResponseEntity.ok(ApiResponse.success("프로필이 변경되었습니다.", responseDto));
    }

    @Operation(summary = "비밀번호 변경", description = "로그인한 사용자의 비밀번호를 변경합니다.", security = {@SecurityRequirement(name = "bearer-key")})
    @PatchMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @RequestBody @Valid UpdatePasswordRequestDto requestDto,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto) {
        userService.updatePassword(userInfoDto.getUserId(), requestDto);
        return ResponseEntity.ok(ApiResponse.success("비밀번호가 변경되었습니다."));
    }

    @Operation(summary = "회원 탈퇴", description = "로그인한 사용자의 계정을 soft 삭제 합니다. (status.DELETED로 변경, 30일 후 DB에서 완전 삭제)", security = {@SecurityRequirement(name = "bearer-key")})
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> softDelete(
            @RequestBody WithdrawalRequestDto requestDto,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto
    ) {
        userService.softDelete(userInfoDto.getUserId(), requestDto);
        return ResponseEntity.ok(ApiResponse.success("계정이 삭제되었습니다."));
    }

}

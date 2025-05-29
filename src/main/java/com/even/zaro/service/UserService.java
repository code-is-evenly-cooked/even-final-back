package com.even.zaro.service;

import com.even.zaro.dto.user.UpdateNicknameRequestDto;
import com.even.zaro.dto.user.UpdateNicknameResponseDto;
import com.even.zaro.dto.user.UpdatePasswordRequestDto;
import com.even.zaro.dto.user.UserInfoResponseDto;
import com.even.zaro.entity.Status;
import com.even.zaro.entity.User;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.user.UserException;
import com.even.zaro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[_!@#$%^&*])[A-Za-z\\d_!@#$%^&*]{6,}$";
    private static final String NICKNAME_REGEX = "^[a-zA-Z0-9가-힣_-]{2,12}$";

    public final UserRepository userRepository;
    public final PasswordEncoder passwordEncoder;

    public UserInfoResponseDto getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        return UserInfoResponseDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .birthday(user.getBirthday())
                .liveAloneDate(user.getLiveAloneDate())
                .gender(user.getGender())
                .mbti(user.getMbti())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .provider(user.getProvider().name())
                .isValidated(user.isValidated())
                .build();
    }

    @Transactional
    public UpdateNicknameResponseDto updateNickname(Long userId, UpdateNicknameRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        if (user.getStatus() == Status.PENDING) {
            throw new UserException(ErrorCode.MAIL_NOT_VERIFIED);
        }

        String newNickname = requestDto.getNewNickname();

        if (newNickname == null || newNickname.isBlank()) {
            throw new UserException(ErrorCode.NEW_NICKNAME_REQUIRED);
        }

        if (!Pattern.matches(NICKNAME_REGEX, newNickname)) {
            throw new UserException(ErrorCode.INVALID_NICKNAME_FORMAT);
        }

        if (userRepository.existsByNickname(newNickname)) {
            throw new UserException(ErrorCode.NICKNAME_ALREADY_EXISTED);
        }

        if (user.getNickname().equals(newNickname)) {
            throw new UserException(ErrorCode.NEW_NICKNAME_EQUALS_ORIGINAL_NICKNAME);
        }

        LocalDateTime now = LocalDateTime.now();
        boolean canUpdate = user.getLastNicknameUpdatedAt() == null
                || user.getLastNicknameUpdatedAt().plusDays(14).isBefore(now);

        if (!canUpdate) {
            throw new UserException(ErrorCode.NICKNAME_UPDATE_COOLDOWN);
        }

        user.updateNickname(newNickname);
        user.updateLastNicknameUpdatedAt(now);

        return new UpdateNicknameResponseDto(
                newNickname,
                user.getLastNicknameUpdatedAt().plusDays(14)
        );
    }

    @Transactional
    public void updatePassword(Long userId, UpdatePasswordRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        if (user.getStatus() == Status.PENDING) {
            throw new UserException(ErrorCode.MAIL_NOT_VERIFIED);
        }

        String currentPassword = requestDto.getCurrentPassword();
        String newPassword = requestDto.getNewPassword();

        if (currentPassword == null || currentPassword.isBlank()) {
            throw new UserException(ErrorCode.CURRENT_PASSWORD_REQUIRED);
        }

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new UserException(ErrorCode.CURRENT_PASSWORD_WRONG);
        }

        validateNewPassword(currentPassword, newPassword);

        user.updatePassword(passwordEncoder.encode(newPassword));
    }

    private void validateNewPassword(String currentPassword, String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            throw new UserException(ErrorCode.PASSWORD_REQUIRED);
        }

        if (!Pattern.matches(PASSWORD_REGEX, newPassword)) {
            throw new UserException(ErrorCode.INVALID_PASSWORD_FORMAT);
        }

        if (currentPassword.equals(newPassword)) {
            throw new UserException(ErrorCode.CURRENT_PASSWORD_EQUALS_NEW_PASSWORD);
        }
    }
}

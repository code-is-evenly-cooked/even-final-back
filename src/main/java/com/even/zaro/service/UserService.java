package com.even.zaro.service;

import com.even.zaro.dto.user.*;
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
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[_!@#$%^&*])[A-Za-z\\d_!@#$%^&*]{6,}$";
    private static final String NICKNAME_REGEX = "^[a-zA-Z0-9가-힣_-]{2,12}$";

    public final UserRepository userRepository;
    public final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserInfoResponseDto getMyInfo(Long userId) {
        User user = findUserById(userId);

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
    public UpdateProfileImageResponseDto updateProfileImage(Long userId, UpdateProfileImageRequestDto requestDto) {
        User user = findUserById(userId);
        validateNotPending(user);

        user.updateProfileImage(requestDto.getProfileImage());

        return new UpdateProfileImageResponseDto(user.getProfileImage());
    }

    @Transactional
    public UpdateNicknameResponseDto updateNickname(Long userId, UpdateNicknameRequestDto requestDto) {
        User user = findUserById(userId);
        validateNotPending(user);

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
        LocalDateTime lastNicknameUpdatedAt = user.getLastNicknameUpdatedAt();

        boolean canUpdate = lastNicknameUpdatedAt == null
                || lastNicknameUpdatedAt.plusDays(14).isBefore(now);

        if (!canUpdate) {
            LocalDateTime nextAvailableDate = lastNicknameUpdatedAt.plusDays(14);
            String formatted = nextAvailableDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"));
            throw new UserException(ErrorCode.NICKNAME_UPDATE_COOLDOWN,
                    String.format("%s 이후에 다시 변경할 수 있습니다.", formatted));
        }

        user.updateNickname(newNickname);
        user.updateLastNicknameUpdatedAt(now);

        return new UpdateNicknameResponseDto(
                newNickname,
                user.getLastNicknameUpdatedAt().plusDays(14)
        );
    }

    @Transactional
    public UpdateProfileResponseDto updateProfile(Long userId, UpdateProfileRequestDto requestDto) {
        User user = findUserById(userId);
        validateNotPending(user);

        user.updateBirthday(requestDto.getBirthday());
        user.updateLiveAloneDate(requestDto.getLiveAloneDate());
        user.updateGender(requestDto.getGender());
        user.updateMbti(requestDto.getMbti());

        return new UpdateProfileResponseDto(
                user.getBirthday(),
                user.getLiveAloneDate(),
                user.getGender(),
                user.getMbti()
        );
    }

    @Transactional
    public void updatePassword(Long userId, UpdatePasswordRequestDto requestDto) {
        User user = findUserById(userId);
        validateNotPending(user);

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

    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
    }

    public void validateNotPending(User user) {
        if (user.getStatus() == Status.PENDING) {
            throw new UserException(ErrorCode.MAIL_NOT_VERIFIED);
        }
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

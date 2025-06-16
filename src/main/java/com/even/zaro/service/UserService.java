package com.even.zaro.service;

import com.even.zaro.dto.user.*;
import com.even.zaro.entity.Status;
import com.even.zaro.entity.User;
import com.even.zaro.entity.WithdrawalHistory;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.user.UserException;
import com.even.zaro.repository.UserRepository;
import com.even.zaro.repository.WithdrawalHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;
    private final WithdrawalHistoryRepository withdrawalHistoryRepository;

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
                .createdAt(user.getCreatedAt().atOffset(ZoneOffset.UTC))
                .updatedAt(user.getUpdatedAt().atOffset(ZoneOffset.UTC))
                .lastLoginAt(user.getLastLoginAt().atOffset(ZoneOffset.UTC))
                .provider(user.getProvider().name())
                .isValidated(user.isValidated())
                .build();
    }

    @Transactional
    public UpdateProfileImageResponseDto updateProfileImage(Long userId, UpdateProfileImageRequestDto requestDto) {
        User user = findUserById(userId);
        validateActiveUser(user);

        user.updateProfileImage(requestDto.getProfileImage());

        return new UpdateProfileImageResponseDto(user.getProfileImage());
    }

    @Transactional
    public UpdateNicknameResponseDto updateNickname(Long userId, UpdateNicknameRequestDto requestDto) {
        User user = findUserById(userId);
        validateActiveUser(user);

        String newNickname = requestDto.getNewNickname();

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
                user.getLastNicknameUpdatedAt().plusDays(14).toLocalDate()
        );
    }

    @Transactional
    public UpdateProfileResponseDto updateProfile(Long userId, UpdateProfileRequestDto requestDto) {
        User user = findUserById(userId);
        validateActiveUser(user);

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
        validateActiveUser(user);

        String currentPassword = requestDto.getCurrentPassword();
        String newPassword = requestDto.getNewPassword();

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new UserException(ErrorCode.CURRENT_PASSWORD_WRONG);
        }

        if (currentPassword.equals(newPassword)) {
            throw new UserException(ErrorCode.CURRENT_PASSWORD_EQUALS_NEW_PASSWORD);
        }

        user.updatePassword(passwordEncoder.encode(newPassword));
    }

    // 회원 탈퇴
    @Transactional
    public void softDelete(Long userId, WithdrawalRequestDto requestDto) {
        User user = findUserById(userId);

        if (user.getStatus() == Status.DELETED) {
            throw new UserException(ErrorCode.USER_ALREADY_DELETED);
        }

        user.softDeleted();
        redisTemplate.delete("refresh:" + userId.toString());

        String reason = requestDto.getReason();

        if (reason != null && !reason.isBlank()) {
            withdrawalHistoryRepository.save(
                    WithdrawalHistory.builder()
                            .reason(reason)
                            .deletedAt(user.getDeletedAt())
                            .createdAt(user.getCreatedAt())
                            .lastLoginAt(user.getLastLoginAt())
                            .liveAloneDate(user.getLiveAloneDate())
                            .birthday(user.getBirthday())
                            .gender(user.getGender())
                            .mbti(user.getMbti())
                            .build()
            );
        }
    }

    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
    }

    public void validateActiveUser(User user) {
        if (user.getStatus() == Status.PENDING) {
            throw new UserException(ErrorCode.MAIL_NOT_VERIFIED);
        } else if (user.getStatus() == Status.DORMANT) {
            throw new UserException(ErrorCode.DORMANT_USER);
        } else if (user.getStatus() == Status.DELETED) {
            throw new UserException(ErrorCode.DELETED_USER);
        }
    }
}

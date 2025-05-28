package com.even.zaro.service;

import com.even.zaro.dto.user.UserInfoResponseDto;
import com.even.zaro.entity.User;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.global.exception.user.UserException;
import com.even.zaro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    public final UserRepository userRepository;

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
}

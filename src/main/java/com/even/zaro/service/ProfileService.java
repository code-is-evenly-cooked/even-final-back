package com.even.zaro.service;

import com.even.zaro.dto.UserProfileDto;
import com.even.zaro.entity.User;
import com.even.zaro.global.ErrorCode;
import com.even.zaro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {
    private final UserRepository userRepository;

    public UserProfileDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException(ErrorCode.USER_EXCEPTION.getDefaultMessage()));

        String dDay = "D+500"; // 수정필요 (계산로직추가구현)
        int postCount = 0; // 수정필요2 (추가구현)

        return UserProfileDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .liveAloneDate(user.getLiveAloneDate())
                .dDay(dDay)
                .mbti(user.getMbti())
                .postCount(postCount)
                .followingCount(user.getFollowingCount())
                .followerCount(user.getFollowerCount())
                .build();
    }
}

package com.even.zaro.service;

import com.even.zaro.dto.profileDto.CreateGroupRequest;
import com.even.zaro.entity.Favorite;
import com.even.zaro.entity.FavoriteGroup;
import com.even.zaro.entity.User;
import com.even.zaro.global.ApiResponse;
import com.even.zaro.global.exception.userEx.UserException;
import com.even.zaro.repository.FavoriteGroupRepository;
import com.even.zaro.repository.FavoriteRepository;
import com.even.zaro.repository.PlaceRepository;
import com.even.zaro.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;

@Service
@Slf4j
@AllArgsConstructor
public class ProfileService {
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final FavoriteGroupRepository favoriteGroupRepository;

    @Transactional
    public void createGroup(CreateGroupRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(UserException::NotFoundUserException);

        FavoriteGroup favoriteGroup = FavoriteGroup.builder()
                .user(user) // 유저 설정
                .name(request.getName()) // Group 이름 설정
                .updatedAt(LocalDateTime.now())
                .build();

        favoriteGroupRepository.save(favoriteGroup);
    }
}

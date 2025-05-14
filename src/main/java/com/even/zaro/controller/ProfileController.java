package com.even.zaro.controller;

import com.even.zaro.dto.profileDto.GroupCreateRequest;
import com.even.zaro.dto.profileDto.GroupEditRequest;
import com.even.zaro.dto.profileDto.GroupResponse;
import com.even.zaro.global.ApiResponse;
import com.even.zaro.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "프로필 페이지", description = "프로필 페이지 API")
public class ProfileController {
    private final ProfileService profileService;

    @Operation(summary = "즐겨찾기 그룹 리스트 조회", description = "userId로 즐겨찾기 그룹 리스트를 조회합니다.")
    @GetMapping("/group")
    public ResponseEntity<ApiResponse<List<GroupResponse>>> getFavoriteGroups(@RequestParam("userId") long userId) {
        List<GroupResponse> groupList = profileService.getFavoriteGroups(userId);

        return ResponseEntity.ok(ApiResponse.success("그룹 리스트를 조회했습니다.", groupList));
    }

    @Operation(summary = "즐겨찾기 그룹 추가", description = "userId와 그룹이름을 입력받아 그룹을 추가합니다.")
    @PostMapping("/group")
    public ResponseEntity<ApiResponse<String>> createGroup(@RequestBody GroupCreateRequest request) {
        profileService.createGroup(request);

        return ResponseEntity.ok(ApiResponse.success("성공적으로 그룹이 생성되었습니다."));
    }

    @Operation(summary = "즐겨찾기 그룹 삭제", description = "groupId로 즐겨찾기 그룹 리스트를 삭제합니다.")
    @DeleteMapping("/group/{groupId}")
    public ResponseEntity<ApiResponse<String>> deleteGroup(@PathVariable("groupId") long groupId) {
        profileService.deleteGroup(groupId);

        return ResponseEntity.ok(ApiResponse.success("성공적으로 그룹을 삭제했습니다."));
    }

    @Operation(summary = "즐겨찾기 그룹 수정", description = "userId와 그룹이름을 입력받아 그룹을 수정합니다.")
    @PatchMapping("/group/{groupId}")
    public ResponseEntity<ApiResponse<String>> editGroup(@RequestBody GroupEditRequest request) {
        profileService.editGroup(request);

        return ResponseEntity.ok(ApiResponse.success("성공적으로 그룹을 수정했습니다."));
    }

}

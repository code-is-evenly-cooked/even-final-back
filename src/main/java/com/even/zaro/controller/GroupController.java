package com.even.zaro.controller;

import com.even.zaro.dto.group.GroupCreateRequest;
import com.even.zaro.dto.group.GroupEditRequest;
import com.even.zaro.dto.group.GroupResponse;
import com.even.zaro.dto.jwt.JwtUserInfoDto;
import com.even.zaro.global.ApiResponse;
import com.even.zaro.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/group")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "그룹", description = "그룹 API")
public class GroupController {

    private final GroupService groupService;


    @Operation(summary = "다른 사용자의 그룹 리스트 조회", description = "사용자는 해당 유저의 프로필에서 그룹 목록을 조회할 수 있다", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping("/user/{userId}/group")
    public ResponseEntity<ApiResponse<List<GroupResponse>>> getFavoriteGroupsByUserId(
            @PathVariable("userId") long userId,
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto) {
        List<GroupResponse> groupList = groupService.getFavoriteGroups(userId);

        return ResponseEntity.ok(ApiResponse.success("해당 유저의 즐겨찾기 그룹 리스트를 조회했습니다.", groupList));
    }


    @Operation(summary = "내 그룹 리스트 조회", description = "내 그룹 리스트를 조회합니다.", security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping
    public ResponseEntity<ApiResponse<List<GroupResponse>>> getMyFavoriteGroups(
            @AuthenticationPrincipal JwtUserInfoDto userInfoDto) {
        List<GroupResponse> groupList = groupService.getFavoriteGroups(userInfoDto.getUserId());

        return ResponseEntity.ok(ApiResponse.success("나의 즐겨찾기 그룹 리스트를 조회했습니다.", groupList));
    }

    @Operation(summary = "그룹 추가", description = "그룹 이름을 입력받고 그룹을 생성합니다.", security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping
    public ResponseEntity<ApiResponse<String>> createGroup(@RequestBody GroupCreateRequest request,
                                                           @AuthenticationPrincipal JwtUserInfoDto userInfoDto) {
        long userId = userInfoDto.getUserId();
        groupService.createGroup(request, userId);

        return ResponseEntity.ok(ApiResponse.success("성공적으로 즐겨찾기 그룹이 생성되었습니다."));
    }

    @Operation(summary = "즐겨찾기 그룹 삭제", description = "groupId로 즐겨찾기 그룹 리스트를 삭제합니다.", security = {@SecurityRequirement(name = "bearer-key")})
    @DeleteMapping("/{groupId}")
    public ResponseEntity<ApiResponse<String>> deleteGroup(@PathVariable("groupId") long groupId,
                                                           @AuthenticationPrincipal JwtUserInfoDto userInfoDto) {
        groupService.deleteGroup(groupId, userInfoDto.getUserId());

        return ResponseEntity.ok(ApiResponse.success("성공적으로 그룹을 삭제했습니다."));
    }

    @Operation(summary = "즐겨찾기 그룹 수정", description = "그룹 이름을 수정합니다.", security = {@SecurityRequirement(name = "bearer-key")})
    @PatchMapping("/{groupId}")
    public ResponseEntity<ApiResponse<String>> editGroup(@PathVariable("groupId") long groupId,
                                                         @RequestBody GroupEditRequest request,
                                                         @AuthenticationPrincipal JwtUserInfoDto userInfoDto) {
        groupService.editGroup(groupId, request, userInfoDto.getUserId());

        return ResponseEntity.ok(ApiResponse.success("성공적으로 그룹 이름을 수정했습니다."));
    }
}

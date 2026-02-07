package likelion14th.lte.follow.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


import likelion14th.lte.follow.dto.FollowUserRequest;
import likelion14th.lte.follow.dto.FollowUserResponse;
import likelion14th.lte.global.api.ApiResponse;
import likelion14th.lte.global.api.SuccessCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import likelion14th.lte.follow.service.FollowService;

@RestController
@Slf4j
@RequestMapping("/api/Fallow")
@RequiredArgsConstructor
@Tag(name = "팔로우 Api", description = "팔로우 추가 및 삭제, 조회를 담당하는 api 입니다.")
public class FollowController {
    private final FollowService followService;
    private final UserDetailsService userDetailsService;


    @PostMapping
    @Operation(summary = "팔로우 추가", description = "유저 id를 통해 팔로우를 추가합니다")
    public ApiResponse<FollowUserResponse> addFollow(
            @AuthenticationPrincipal Long userId,
            @RequestBody FollowUserRequest followUserRequest) {
       FollowUserResponse response = followService.followUser(userId, followUserRequest.getToUserId());
       return ApiResponse.onSuccess(SuccessCode.FOLLOW_ADD_SUCCESS, response);
    }
/*
    @DeleteMapping
    @Operation(summary = "언팔로우", description = "")
*/

}

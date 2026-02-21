package likelion14th.lte.follow.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;


import likelion14th.lte.follow.dto.FollowUserRequest;
import likelion14th.lte.follow.dto.FollowUserResponse;
import likelion14th.lte.global.api.ApiResponse;
import likelion14th.lte.global.api.SuccessCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import likelion14th.lte.follow.service.FollowService;

import java.util.List;


/**
 * 팔로우/언팔로우 및 목록·검색 API를 제공하는 REST 컨트롤러.
 *
 * @RestController: HTTP 요청/응답 처리, 반환값을 JSON 등으로 직렬화(뷰 이름 아님).
 * @Slf4j: 로거(log) 자동 생성. log.info(), log.debug() 등 사용 가능.
 * @RequestMapping("/api/follow"): 이 컨트롤러의 모든 매핑 URL prefix.
 * @RequiredArgsConstructor: final 필드에 대한 생성자 생성 → FollowService 등 의존성 주입에 사용.
 * @Tag: Swagger/OpenAPI 문서에서 이 API 그룹의 이름과 설명 표시.
 */
@RestController
@Slf4j
@RequestMapping("/api/follow")
@RequiredArgsConstructor
@Tag(name = "팔로우 Api", description = "팔로우 추가 및 삭제, 조회를 담당하는 api 입니다.")
public class FollowController {
    private final FollowService followService;

    /**  @PostMapping: HTTP POST 요청 매핑. 요청 바디에 JSON을 담아 보냄 */
    @PostMapping
    @Operation(summary = "팔로우 추가", description = "유저 id를 통해 팔로우를 추가합니다")
    public ApiResponse<FollowUserResponse> addFollow(
            @AuthenticationPrincipal Long userId,
            @RequestBody FollowUserRequest followUserRequest) {
       FollowUserResponse response = followService.followUser(userId, followUserRequest.getToUserId());
       return ApiResponse.onSuccess(SuccessCode.FOLLOW_ADD_SUCCESS, response);
    }

    /**  @DeleteMapping: HTTP DELETE 요청 매핑. 리소스 삭제 시 사용 */
    @DeleteMapping
    @Operation(summary = "언팔로우", description = "유저 id를 통해 언팔로우를 진행합니다")
    public ApiResponse<Void> deleteFollow(
            @AuthenticationPrincipal Long userId,
            @RequestBody FollowUserRequest followUserRequest){
        followService.unfollowUser(userId, followUserRequest.getToUserId());
        return ApiResponse.onSuccess(SuccessCode.FOLLOW_DELETE_SUCCESS, null);
    }

    /**  GET /api/follow/followers: 나를 팔로우하는 사람 목록(팔로워) 조회 */
    @GetMapping("/followers")
    public ApiResponse<List<FollowUserResponse>> getFollows(
            @AuthenticationPrincipal Long userId
    ){
        List<FollowUserResponse> response = followService.getFollowers(userId);
        return ApiResponse.onSuccess(SuccessCode.FOLLOW_LIST_GET_SUCCESS, response);
    }

    /**  GET /api/follow/followings: 내가 팔로우하는 사람 목록(팔로잉) 조회 */
    @GetMapping ("/followings")
    public ApiResponse<List<FollowUserResponse>> getFollowings(
            @AuthenticationPrincipal Long userId
    ){
        List<FollowUserResponse> response = followService.getFollowings(userId);
        return ApiResponse.onSuccess(SuccessCode.FOLLOW_LIST_GET_SUCCESS, response);
    }

    /**
     *  GET /api/follow/search: 닉네임으로 팔로우 가능한 유저 검색.
     * @RequestParam: 쿼리 파라미터(nickname=값) 바인딩.
     * @ParameterObject + @PageableDefault: Spring Doc에서 pageable 파라미터를 문서화하고, 기본값 page=0, size=10 적용.
     */
    @GetMapping("/search")
    @Operation(summary = "팔로우 가능한 유저 검색", description = "닉네임으로 팔로우 가능한 유저를 검색합니다. 쿼리 파라미터로 page, size, sort를 전달할 수 있습니다. " +
            "sort 파라미터는 선택사항이며, 형식: sort=id,DESC 또는 sort=username,ASC (쉼표로 구분). " +
            "정렬 가능한 필드: id, username, userTag, createdAt, updatedAt")
    public ApiResponse<Page<FollowUserResponse>> getSearchFollows(
            @AuthenticationPrincipal Long userId,
            @RequestParam String nickname,
            @ParameterObject @PageableDefault(size = 10, page = 0) Pageable pageable
            ){
        Page<FollowUserResponse> response = followService.serchCanFollowers(userId, nickname, pageable);
        return ApiResponse.onSuccess(SuccessCode.FOLLOW_SEARCH_SUCCESS, response);
    }

    /**
     *  GET /api/follow: 팔로우 가능한 유저 목록 페이징 조회.
     * @AuthenticationPrincipal: Spring Security에 의해 인증된 사용자 정보 주입(여기서는 userId로 설정된 경우).
     */
    @GetMapping
    @Operation(summary = "팔로우 가능한 유저를 조회합니다.", description = "팔로우 가능한 유저를 페이징하며 조회합니다. " +
            "쿼리 파라미터로 page, size, sort를 전달할 수 있습니다. " +
            "sort 파라미터는 선택사항이며, 형식: sort=id,DESC 또는 sort=username,ASC (쉼표로 구분). " +
            "정렬 가능한 필드: id, username, userTag, createdAt, updatedAt")
    public ApiResponse<Page<FollowUserResponse>> getCanFollowUsers(
            @AuthenticationPrincipal Long userId,
            @ParameterObject @PageableDefault(size = 10, page = 0) Pageable pageable
    ){
        Page<FollowUserResponse> responses = followService.getCanFollowers(userId, pageable);
        return ApiResponse.onSuccess(SuccessCode.FOLLOW_SEARCH_SUCCESS, responses);
    }
}

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
 * 팔로우 관련 HTTP 요청을 받아 처리하는 REST 컨트롤러입니다.
 * 클라이언트의 요청을 받아 서비스 계층에 위임하고, 그 결과를 정해진 API 형식(JSON)으로 반환하는 역할을 합니다.
 *
 * @RestController : 이 클래스를 REST API 컨트롤러로 등록하며, 반환값을 뷰가 아닌 HTTP 응답 본문(예: JSON)으로 직렬화합니다.
 * @Slf4j : 로깅을 위한 Logger를 주입합니다.
 * @RequestMapping("/api/follow") : 이 컨트롤러가 처리하는 URL의 공통 prefix를 지정합니다.
 * @RequiredArgsConstructor : final 필드에 대한 생성자를 생성하며, 생성자 주입으로 의존성을 주입받을 수 있게 합니다.
 * @Tag : Swagger/OpenAPI 문서에서 이 API 그룹의 이름과 설명을 표시할 때 사용합니다.
 */
@RestController
@Slf4j
@RequestMapping("/api/follow")
@RequiredArgsConstructor
@Tag(name = "팔로우 Api", description = "팔로우 추가 및 삭제, 조회를 담당하는 api 입니다.")
public class FollowController {
    private final FollowService followService;

    /** POST /api/follow : 팔로우 추가 요청입니다. 요청 본문의 toUserId가 팔로우 대상 유저 ID입니다. */
    @PostMapping
    @Operation(summary = "팔로우 추가", description = "유저 id를 통해 팔로우를 추가합니다")
    public ApiResponse<FollowUserResponse> addFollow(
            @RequestParam Long userId,
            @RequestBody FollowUserRequest followUserRequest) {
       // 서비스에 "로그인한 유저 ID"와 "팔로우할 대상 ID"를 넘겨 팔로우를 추가하고, 추가된 대상 유저 정보를 응답 DTO로 받습니다.
       FollowUserResponse response = followService.followUser(userId, followUserRequest.getToUserId());
       // 성공 코드와 응답 데이터를 담은 ApiResponse를 반환합니다. (클라이언트는 이걸 JSON으로 받습니다)
       return ApiResponse.onSuccess(SuccessCode.FOLLOW_ADD_SUCCESS, response);
    }

    /** DELETE /api/follow : 언팔로우 요청입니다. */
    @DeleteMapping
    @Operation(summary = "언팔로우", description = "유저 id를 통해 언팔로우를 진행합니다")
    public ApiResponse<Void> deleteFollow(
            @RequestParam Long userId,
            @RequestBody FollowUserRequest followUserRequest){
        // 서비스에서 언팔로우 처리만 수행합니다. (반환 데이터 없음)
        followService.unfollowUser(userId, followUserRequest.getToUserId());
        return ApiResponse.onSuccess(SuccessCode.FOLLOW_DELETE_SUCCESS, null);
    }

    /** GET /api/follow/followers : 나를 팔로우하는 사람 목록(팔로워)을 조회합니다. */
    @GetMapping("/followers")
    public ApiResponse<List<FollowUserResponse>> getFollows(
            @RequestParam Long userId
    ){
        // 서비스에서 "나를 팔로우하는 사람들" 목록을 조회한 뒤, 성공 응답으로 감싸서 반환합니다.
        List<FollowUserResponse> response = followService.getFollowers(userId);
        return ApiResponse.onSuccess(SuccessCode.FOLLOW_LIST_GET_SUCCESS, response);
    }

    /** GET /api/follow/followings : 내가 팔로우하는 사람 목록(팔로잉)을 조회합니다. */
    @GetMapping ("/followings")
    public ApiResponse<List<FollowUserResponse>> getFollowings(
            @RequestParam Long userId
    ){
        // 서비스에서 "내가 팔로우하는 사람들" 목록을 조회한 뒤, 성공 응답으로 감싸서 반환합니다.
        List<FollowUserResponse> response = followService.getFollowings(userId);
        return ApiResponse.onSuccess(SuccessCode.FOLLOW_LIST_GET_SUCCESS, response);
    }

    /**
     * GET /api/follow/search : 닉네임으로 팔로우 가능한 유저를 검색합니다.
     * @RequestParam nickname : 쿼리 파라미터 nickname(예: ?nickname=홍길동)을 이 파라미터에 바인딩합니다.
     * @PageableDefault : page, size 등이 전달되지 않으면 기본값(page=0, size=10)을 적용합니다.
     */
    @GetMapping("/search")
    @Operation(summary = "팔로우 가능한 유저 검색", description = "닉네임으로 팔로우 가능한 유저를 검색합니다. 쿼리 파라미터로 page, size, sort를 전달할 수 있습니다. " +
            "sort 파라미터는 선택사항이며, 형식: sort=id,DESC 또는 sort=username,ASC (쉼표로 구분). " +
            "정렬 가능한 필드: id, username, userTag, createdAt, updatedAt")
    public ApiResponse<Page<FollowUserResponse>> getSearchFollows(
            @RequestParam Long userId,
            @RequestParam String nickname,
            @ParameterObject @PageableDefault(size = 10, page = 0) Pageable pageable
            ){
        // 서비스에서 닉네임으로 팔로우 가능한 유저를 검색하고, 페이징된 결과를 받아 성공 응답으로 반환합니다.
        Page<FollowUserResponse> response = followService.serchCanFollowers(userId, nickname, pageable);
        return ApiResponse.onSuccess(SuccessCode.FOLLOW_SEARCH_SUCCESS, response);
    }

    /**
     * GET /api/follow : 팔로우 가능한 유저 목록을 페이징하여 조회합니다.
     * @AuthenticationPrincipal : 현재 인증된 사용자 정보를 주입합니다. 여기서는 로그인한 사용자의 ID(Long)가 바인딩됩니다.
     */
    @GetMapping
    @Operation(summary = "팔로우 가능한 유저를 조회합니다.", description = "팔로우 가능한 유저를 페이징하며 조회합니다. " +
            "쿼리 파라미터로 page, size, sort를 전달할 수 있습니다. " +
            "sort 파라미터는 선택사항이며, 형식: sort=id,DESC 또는 sort=username,ASC (쉼표로 구분). " +
            "정렬 가능한 필드: id, username, userTag, createdAt, updatedAt")
    public ApiResponse<Page<FollowUserResponse>> getCanFollowUsers(
            @RequestParam Long userId,
            @ParameterObject @PageableDefault(size = 10, page = 0) Pageable pageable
    ){
        // 서비스에서 팔로우 가능한 유저 목록을 페이징 조회한 뒤, 성공 응답으로 반환합니다.
        Page<FollowUserResponse> responses = followService.getCanFollowers(userId, pageable);
        return ApiResponse.onSuccess(SuccessCode.FOLLOW_SEARCH_SUCCESS, responses);
    }
}

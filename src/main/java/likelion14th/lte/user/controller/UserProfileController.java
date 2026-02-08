package likelion14th.lte.user.controller;

import jakarta.servlet.annotation.MultipartConfig;
import likelion14th.lte.user.dto.response.UserProfileResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import likelion14th.lte.global.api.SuccessCode;
import likelion14th.lte.global.api.ApiResponse;

import likelion14th.lte.user.service.UserProfileService;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;

@RestController
@Slf4j
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Tag(name="유저 프로필 API", description = "유저 프로필 CUD 담당하는 api 입니다")
public class UserProfileController {
    public final UserProfileService userProfileService;

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "유저 프로필 추가 및 수정", description = "유저 프로필 이미지를 추가하거나 수정합니다")
    public ApiResponse<UserProfileResponse> putUserProfile(
            @AuthenticationPrincipal Long userId,
            @RequestParam("image") MultipartFile image){

        UserProfileResponse response=userProfileService.putProfileImage(userId, image);
        return ApiResponse.onSuccess(SuccessCode.PROFILE_PUT_SUCCESS,response);
    }

    @DeleteMapping
    @Operation(summary = "유저 프로필 삭제", description = "유저 프로필 이미지를 삭제합니다.")
    public ApiResponse<UserProfileResponse> deleteUserProfile(
            @AuthenticationPrincipal Long userId){
        UserProfileResponse response = userProfileService.deleteProfileImage(userId);
        return ApiResponse.onSuccess(SuccessCode.PROFILE_DELETE_SUCCESS,response);
    }
}

package likelion14th.lte.login.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import likelion14th.lte.global.api.ApiResponse;
import likelion14th.lte.global.api.SuccessCode;
import likelion14th.lte.login.dto.request.KakaoCodeRequest;
import likelion14th.lte.login.dto.response.AuthResponse;
import likelion14th.lte.login.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/kakao")
    @Operation(summary = "카카오 로그인 처리 (인가코드 전달)",
            description = "프론트에서 전달한 카카오 인가코드(code)로 자체 Access/Refresh Token을 발급합니다.")
    public ResponseEntity<ApiResponse<AuthResponse>> kakaoLogin(
            @RequestBody KakaoCodeRequest request) {

        // 카카오 로그인: code로 카카오 토큰 발급 -> 유저 조회/저장 -> 우리 JWT 발급
        AuthResponse response = authService.handleKakaoCode(request.getCode());

        return ResponseEntity.ok(
                ApiResponse.onSuccess(SuccessCode.USER_LOGIN_SUCCESS, response)
        );
    }

    @PostMapping("/reissue")
    @Operation(summary = "accessToken 재발급")
    public ResponseEntity<ApiResponse<String>> reissue(HttpServletRequest request) {

        // 재발급: refreshToken 으로 accessToken 재발급
        String response = authService.reissueAccessToken(request);

        return ResponseEntity.ok(
                ApiResponse.onSuccess(SuccessCode.USER_REISSUE_SUCCESS, response));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "DB에 저장된 Refresh Token을 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {

        // 로그아웃
        authService.logout(request);

        return ResponseEntity.ok(
                ApiResponse.onSuccess(SuccessCode.USER_LOGOUT_SUCCESS, null)
        );
    }

    @DeleteMapping("/withdraw")
    @Operation(summary = "회원탈퇴")
    public ResponseEntity<ApiResponse<Void>> withdraw(HttpServletRequest request) {

        // 회원탈퇴: refresh token 삭제 후 user 삭제
        authService.withdraw(request);

        return ResponseEntity.ok(
                ApiResponse.onSuccess(SuccessCode.USER_DELETE_SUCCESS, null)
        );
    }
}

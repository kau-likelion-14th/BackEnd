package likelion14th.lte.login.controller;

import io.swagger.v3.oas.annotations.Operation;
import likelion14th.lte.global.api.ApiResponse;
import likelion14th.lte.global.api.ErrorCode;
import likelion14th.lte.global.api.SuccessCode;
import likelion14th.lte.login.dto.request.KakaoCodeRequest;
import likelion14th.lte.login.dto.response.AuthResponse;
import likelion14th.lte.login.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    // 카카오 로그인: code로 카카오 토큰 발급 → 유저 조회/저장 → 우리 JWT 발급
    public ResponseEntity<ApiResponse<AuthResponse>> kakaoLogin(@RequestBody KakaoCodeRequest request) {

        if (request == null || request.getCode() == null || request.getCode().isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.onFailure(ErrorCode.BAD_REQUEST, null));
        }

        AuthResponse response = authService.handleKakaoCode(request.getCode());
        if (response == null)
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.onFailure(ErrorCode.TOKEN_EXPIRED, null));

        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessCode.USER_LOGIN_SUCCESS, response));
    }
}

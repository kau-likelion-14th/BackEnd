package likelion14th.lte.login.service;

import io.jsonwebtoken.Claims;
import likelion14th.lte.youtube.repository.SavedSongRepository;
import tools.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import likelion14th.lte.global.api.ErrorCode;
import likelion14th.lte.global.exception.GeneralException;
import likelion14th.lte.login.client.KakaoClient;
import likelion14th.lte.login.domain.RefreshToken;
import likelion14th.lte.login.dto.response.AuthResponse;
import likelion14th.lte.login.repository.RefreshTokenRepository;
import likelion14th.lte.user.domain.User;
import likelion14th.lte.user.repository.UserRepository;
import likelion14th.lte.login.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final KakaoClient kakaoClient;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SavedSongRepository savedSongRepository;
    private final JwtProvider jwtProvider;

    public AuthResponse handleKakaoCode(String code,Boolean isDevelop) {

        // code -> 카카오 access_token
        String kakaoAccessToken = kakaoClient.getAccessToken(code,isDevelop);

        // access token -> kakao user info
        JsonNode kakaoUserInfo = kakaoClient.getUserInfo(kakaoAccessToken);

        String providerId = kakaoUserInfo.get("id").asText();
        String username = kakaoUserInfo
                .path("kakao_account")
                .path("profile")
                .path("nickname")
                .asText("유저");

        // 유저 조회 or 생성
        User user = userRepository.findByProviderId(providerId)
                .orElseGet(() -> createUser(providerId, username));

        // JWT 발급
        String accessToken = jwtProvider.createAccessToken(user.getId());
        String refreshToken = jwtProvider.createRefreshToken(user.getId());
        Long refreshTokenExpiration = jwtProvider.getRefreshTokenExpiration();

        // RefreshToken 저장/업데이트
        saveOrUpdateRefreshToken(user, refreshToken, refreshTokenExpiration);

        // 응답 (유저 정보 + accessToken)
        return AuthResponse.from(user, accessToken);
    }

    private User createUser(String providerId, String username) {
        User user = User.builder()
                .providerId(providerId)
                .username(username)
                .userTag(generateUserTag())
                .build();

        return userRepository.save(user);
    }

    private void saveOrUpdateRefreshToken(User user, String token, Long expiration) {
        refreshTokenRepository.findByUser(user)
                .ifPresentOrElse(
                        existing -> existing.updateToken(token, expiration),
                        () -> refreshTokenRepository.save(
                                RefreshToken.builder()
                                        .user(user)
                                        .refreshToken(token)
                                        .refreshTokenExpiration(expiration)
                                        .build()
                        )
                );
    }

    public String reissueAccessToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer == null || !bearer.startsWith("Bearer ")) {
            throw new GeneralException(ErrorCode.TOKEN_INVALID);
        }

        String accessToken = bearer.substring(7);

        // accessToken이 만료돼도 Claims는 뽑을 수 있어야 재발급이 가능함
        Long userId;
        try {
            Claims claims = jwtProvider.parseClaims(accessToken); // 만료면 ExpiredJwtException에서 claims 반환
            String subject = claims.getSubject();
            if (subject == null || subject.isBlank()) {
                throw new GeneralException(ErrorCode.TOKEN_INVALID);
            }
            userId = Long.parseLong(subject);
        } catch (Exception e) {
            throw new GeneralException(ErrorCode.TOKEN_INVALID);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        RefreshToken saved = refreshTokenRepository.findByUser(user)
                .orElseThrow(() -> new GeneralException(ErrorCode.WRONG_REFRESH_TOKEN));

        try {
            jwtProvider.validate(saved.getRefreshToken());
        } catch (Exception e) {
            refreshTokenRepository.delete(saved);
            throw new GeneralException(ErrorCode.TOKEN_EXPIRED);
        }

        return jwtProvider.createAccessToken(userId);
    }

    // TODO 일단 겹치는 유저태그 있는지 확인하는 로직은 안짰습니다. 7자리 -> 8자리로 바꿨구요
    private String generateUserTag() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8)
                .toUpperCase();
    }

    // accessToken 내 userId 추출 후 refreshToken 삭제
    public void logout(HttpServletRequest request) {

        User user = validateUser(request);

        RefreshToken saved = refreshTokenRepository.findByUser(user)
                .orElseThrow(() -> new GeneralException(ErrorCode.WRONG_REFRESH_TOKEN));

        refreshTokenRepository.delete(saved);
    }

    // 유효한 accessToken 으로 인증 후 회원탈퇴
    public void withdraw(HttpServletRequest request) {

        User user = validateUser(request);

        refreshTokenRepository.deleteByUser(user);

        savedSongRepository.deleteByUser(user);

        userRepository.delete(user);
    }

    public User validateUser(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer == null || !bearer.startsWith("Bearer ")) {
            throw new GeneralException(ErrorCode.TOKEN_INVALID);
        }

        String accessToken = bearer.substring(7);

        try {
            jwtProvider.validate(accessToken);
        } catch (Exception e) {
            throw new GeneralException(ErrorCode.TOKEN_INVALID);
        }

        Long userId;

        try {
            userId = jwtProvider.getUserId(accessToken);
        } catch (NumberFormatException e) {
            throw new GeneralException(ErrorCode.TOKEN_INVALID);
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));
    }
}

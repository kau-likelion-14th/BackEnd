package likelion14th.lte.login.service;

import com.fasterxml.jackson.databind.JsonNode;
import likelion14th.lte.login.client.KakaoClient;
import likelion14th.lte.login.domain.RefreshToken;
import likelion14th.lte.login.dto.response.AuthResponse;
import likelion14th.lte.login.repository.RefreshTokenRepository;
import likelion14th.lte.user.domain.User;
import likelion14th.lte.user.repository.UserRepository;
import likelion14th.lte.global.jwt.JwtProvider;
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
    private final JwtProvider jwtProvider;

    public AuthResponse handleKakaoCode(String code) {

        // code -> 카카오 access_token
        String kakaoAccessToken = kakaoClient.getAccessToken(code);

        // access token → kakao user info
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

    private String generateUserTag() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8)
                .toUpperCase();
    }
}

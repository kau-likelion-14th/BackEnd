package likelion14th.lte.login.client;

import com.fasterxml.jackson.databind.JsonNode;
import likelion14th.lte.global.api.ErrorCode;
import likelion14th.lte.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class KakaoClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${kakao.token-uri}")
    private String tokenUri;

    @Value("${kakao.user-info-uri}")
    private String userInfoUri;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    public String getAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(body, headers);

        try {
            JsonNode response = restTemplate.postForObject(
                    tokenUri, request, JsonNode.class
            );

            if (response == null || !response.has("access_token")) {
                throw new GeneralException(ErrorCode.KAKAO_AUTH_FAILED);
            }

            return response.get("access_token").asText();

        } catch (Exception e) {
            throw new GeneralException(ErrorCode.KAKAO_AUTH_FAILED);
        }
    }

    public JsonNode getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    userInfoUri,
                    HttpMethod.GET,
                    request,
                    JsonNode.class
            );

            JsonNode body = response.getBody();
            if (body == null || !body.has("id")) {
                throw new GeneralException(ErrorCode.KAKAO_AUTH_FAILED);
            }

            return body;

        } catch (Exception e) {
            throw new GeneralException(ErrorCode.KAKAO_AUTH_FAILED);
        }
    }
}

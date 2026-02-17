package likelion14th.lte.youtube.client;

import tools.jackson.databind.JsonNode;
import likelion14th.lte.global.api.ErrorCode;
import likelion14th.lte.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class YouTubeClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${youtube.api-key}")
    private String apiKey;

    @Value("${youtube.api-base}")
    private String apiBase;

    // 검색 (videoId, title, channelTitle, thumbnail 등 얻기)
    public JsonNode searchVideosRaw(String query, int limit) {

        String url = UriComponentsBuilder
                .fromUriString(apiBase + "/search")
                .queryParam("part", "snippet")
                .queryParam("q", query)
                .queryParam("type", "video")
                .queryParam("maxResults", limit)
                .queryParam("key", apiKey)
                .build()
                .toUriString();

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(new HttpHeaders()),
                    JsonNode.class
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new GeneralException(ErrorCode.YOUTUBE_API_FAILED);
            }

            return response.getBody();

        } catch (Exception e) {
            throw new GeneralException(ErrorCode.YOUTUBE_API_FAILED);
        }
    }

    // 단건 조회 (id로 snippet/details 뽑기)
    public JsonNode getVideoRaw(String videoId) {

        String url = UriComponentsBuilder
                .fromUriString(apiBase + "/videos")
                .queryParam("part", "snippet,contentDetails")
                .queryParam("id", videoId)
                .queryParam("key", apiKey)
                .build()
                .toUriString();

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(new HttpHeaders()),
                    JsonNode.class
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new GeneralException(ErrorCode.YOUTUBE_API_FAILED);
            }

            return response.getBody();

        } catch (Exception e) {
            throw new GeneralException(ErrorCode.YOUTUBE_API_FAILED);
        }
    }
}

package likelion14th.lte.youtube.service;

import tools.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import likelion14th.lte.global.api.ErrorCode;
import likelion14th.lte.global.exception.GeneralException;
import likelion14th.lte.login.service.AuthService;
import likelion14th.lte.youtube.client.YouTubeClient;
import likelion14th.lte.youtube.domain.SavedSong;
import likelion14th.lte.youtube.dto.response.SavedSongResponse;
import likelion14th.lte.youtube.dto.response.YouTubeSongItemResponse;
import likelion14th.lte.youtube.repository.SavedSongRepository;
import likelion14th.lte.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class YouTubeService {

    private final YouTubeClient youTubeClient;
    private final SavedSongRepository savedSongRepository;
    private final AuthService authService;

    @Transactional(readOnly = true)
    public List<YouTubeSongItemResponse> searchSongs(String query, int limit) {

        JsonNode root = youTubeClient.searchVideosRaw(query, limit);

        JsonNode items = root.path("items");
        List<YouTubeSongItemResponse> result = new ArrayList<>();
        if (!items.isArray()) return result;

        for (JsonNode item : items) {
            String videoId = item.path("id").path("videoId").asText(null);
            if (videoId == null) continue;

            JsonNode snippet = item.path("snippet");

            String title = snippet.path("title").asText("");
            String channelTitle = snippet.path("channelTitle").asText("");
            String thumb = snippet.path("thumbnails").path("high").path("url").asText(null);
            if (thumb == null) {
                thumb = snippet.path("thumbnails").path("default").path("url").asText(null);
            }

            result.add(YouTubeSongItemResponse.builder()
                    .songId(videoId)
                    .title(title)
                    .artist(channelTitle)
                    .imageUrl(thumb)
                    .build());
        }

        return result;
    }

    public SavedSongResponse saveSong(HttpServletRequest request, String songId) {

        User user = authService.validateUser(request);

        // 단건 조회로 정확한 snippet 가져오기
        JsonNode root = youTubeClient.getVideoRaw(songId);
        JsonNode first = root.path("items").isArray() && root.path("items").size() > 0
                ? root.path("items").get(0)
                : null;

        if (first == null) {
            throw new GeneralException(ErrorCode.YOUTUBE_API_FAILED);
        }

        JsonNode snippet = first.path("snippet");

        String title = snippet.path("title").asText("");
        String channelTitle = snippet.path("channelTitle").asText("");
        String album = null;

        String imageUrl = snippet.path("thumbnails").path("high").path("url").asText(null);
        if (imageUrl == null) {
            imageUrl = snippet.path("thumbnails").path("default").path("url").asText(null);
        }

        savedSongRepository.findFirstByUser(user)
                .ifPresent(savedSongRepository::delete);

        // durationMs/previewUrl은 유튜브에서 바로 안 나오니 null 처리(혹은 contentDetails.duration 파싱 확장 가능)
        SavedSong savedSong = SavedSong.builder()
                .user(user)
                .songId(songId)
                .title(title)
                .artist(channelTitle)
                .album(album)
                .imageUrl(imageUrl)
                .previewUrl(null)
                .durationMs(null)
                .build();

        SavedSong created = savedSongRepository.save(savedSong);
        return SavedSongResponse.from(created);
    }

    @Transactional(readOnly = true)
    public List<SavedSongResponse> mySavedSongs(HttpServletRequest request) {
        User user = authService.validateUser(request);

        return savedSongRepository.findFirstByUser(user)
                .map(savedSong -> List.of(SavedSongResponse.from(savedSong)))
                .orElseGet(List::of);
    }

    public void deleteSavedSong(HttpServletRequest request, String songId) {

        // 유저 검증 + 객체 가져오기
        User user = authService.validateUser(request);

        SavedSong song = savedSongRepository
                .findFirstByUser(user)
                .orElseThrow(() -> new GeneralException(ErrorCode.SONG_NOT_FOUND));

        savedSongRepository.delete(song);
    }

}

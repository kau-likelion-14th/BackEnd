package likelion14th.lte.youtube.dto.response;

import likelion14th.lte.youtube.domain.SavedSong;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SavedSongResponse {

    private Long id;
    private String songId;
    private String title;
    private String artist;
    private String album;
    private String imageUrl;
    private String previewUrl;
    private Integer durationMs;
    private LocalDateTime savedAt;

    public static SavedSongResponse from(SavedSong song) {
        return SavedSongResponse.builder()
                .id(song.getId())
                .songId(song.getSongId())
                .title(song.getTitle())
                .artist(song.getArtist())
                .album(song.getAlbum())
                .imageUrl(song.getImageUrl())
                .previewUrl(song.getPreviewUrl())
                .durationMs(song.getDurationMs())
                .savedAt(song.getSavedAt())
                .build();
    }
}

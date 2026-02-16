package likelion14th.lte.youtube.domain;

import jakarta.persistence.*;
import likelion14th.lte.user.domain.User;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "saved_song",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "songId"})
        }
)
public class SavedSong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 64)
    private String songId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 200)
    private String artist;

    @Column(length = 200)
    private String album;

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 500)
    private String previewUrl;

    private Integer durationMs;

    @Column(nullable = false)
    private LocalDateTime savedAt;

    @Builder
    public SavedSong(User user, String songId, String title, String artist,
                     String album, String imageUrl, String previewUrl, Integer durationMs) {
        this.user = user;
        this.songId = songId;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.imageUrl = imageUrl;
        this.previewUrl = previewUrl;
        this.durationMs = durationMs;
        this.savedAt = LocalDateTime.now();
    }
}


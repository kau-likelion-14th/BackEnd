package likelion14th.lte.youtube.repository;

import likelion14th.lte.user.domain.User;
import likelion14th.lte.youtube.domain.SavedSong;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SavedSongRepository extends JpaRepository<SavedSong, String> {
    Optional<SavedSong> findFirstByUser(User user);

    void deleteByUser(User user);
}

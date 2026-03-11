package likelion14th.lte.user.dto.response;

import likelion14th.lte.youtube.dto.response.SavedSongResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import likelion14th.lte.user.domain.User;

import java.util.List;

@Getter
@AllArgsConstructor
public class UserProfileResponse {
    private String userName;
    private String profileImageUrl;
    private String introduction;
    private List<SavedSongResponse> savedSongs;
    public static UserProfileResponse from(User user){
        return new UserProfileResponse(
                user.getUsername()+"#"+user.getUserTag(),
                user.getProfileImage(),
                user.getIntroduction(),
                user.getSavedSongs().stream().map(SavedSongResponse::from).toList()
        );
    }
}

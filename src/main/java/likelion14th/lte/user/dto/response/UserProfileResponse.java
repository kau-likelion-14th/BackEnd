package likelion14th.lte.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import likelion14th.lte.user.domain.User;

@Getter
@AllArgsConstructor
public class UserProfileResponse {
    private String userName;
    private String profileImageUrl;
    private String introduction;
    public static UserProfileResponse from(User user){
        return new UserProfileResponse(
                user.getUsername()+"#"+user.getUserTag(),
                user.getProfileImage(),
                user.getIntroduction()
        );
    }
}

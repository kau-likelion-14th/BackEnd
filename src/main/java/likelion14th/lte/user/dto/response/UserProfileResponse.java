package likelion14th.lte.user.dto.response;

import likelion14th.lte.user.domain.User;
import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class UserProfileResponse {
    private String Url;
    public static UserProfileResponse from(User user){
        return new  UserProfileResponse(
                user.getProfileImage()
        );
    }
}

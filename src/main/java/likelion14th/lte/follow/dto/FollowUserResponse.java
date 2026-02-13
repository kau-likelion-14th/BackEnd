package likelion14th.lte.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import likelion14th.lte.user.domain.User;

@Getter
@AllArgsConstructor
public class FollowUserResponse {
    private Long userId;
    private String userName;
    private String profileImageUrl;
    private String introduction;

    public static FollowUserResponse from(User user){
        return new FollowUserResponse(
                user.getId(),
                user.getUsername()+"#"+user.getUserTag(),
                user.getProfileImage(),
                user.getIntroduction()
        );
    }



}

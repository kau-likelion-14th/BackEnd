package likelion14th.lte.login.dto.response;

import likelion14th.lte.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String introduction;
    private String profileImage;
    private String userTag;
    private String accessToken;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .introduction(user.getIntroduction())
                .profileImage(user.getProfileImage())
                .userTag(user.getUserTag())
                .build();
    }
}

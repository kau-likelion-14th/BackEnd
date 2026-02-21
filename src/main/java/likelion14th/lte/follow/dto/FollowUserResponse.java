package likelion14th.lte.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import likelion14th.lte.user.domain.User;

/**
 * 팔로우 관련 API의 응답으로 사용하는 DTO입니다.
 * 팔로워·팔로잉 목록, 검색 결과 등에서 유저의 요약 정보를 내려줄 때 사용합니다.
 * 엔티티(User)의 모든 필드가 아닌, API에서 노출할 필드만 선별하여 담습니다.
 *
 * @Getter : 응답 객체를 JSON으로 직렬화할 때 getter가 사용됩니다.
 * @AllArgsConstructor : from(User) 정적 메서드에서 생성자를 호출할 때 사용합니다.
 */
@Getter
@AllArgsConstructor
public class FollowUserResponse {
    private Long userId;           // 유저 ID
    private String userName;       // 표시 이름 (닉네임#태그 형태)
    private String profileImageUrl; // 프로필 이미지 URL
    private String introduction;   // 자기소개

    /**
     * User 엔티티를 FollowUserResponse DTO로 변환하는 정적 팩토리 메서드입니다.
     * userName은 "닉네임#태그" 형태로 조합하여 반환합니다.
     */
    public static FollowUserResponse from(User user){
        return new FollowUserResponse(
                user.getId(),                                    // 유저 ID
                user.getUsername()+"#"+user.getUserTag(),       // 표시용 이름: "닉네임#태그" 한 문자열로 합칩니다.
                user.getProfileImage(),                          // 프로필 이미지 URL
                user.getIntroduction()                           // 자기소개
        );
    }
}

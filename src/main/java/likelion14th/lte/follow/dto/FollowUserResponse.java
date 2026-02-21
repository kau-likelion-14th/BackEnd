package likelion14th.lte.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import likelion14th.lte.user.domain.User;

/**
 *  팔로우 관련 API 응답용 DTO.
 * 팔로워/팔로잉 목록, 검색 결과 등에서 "유저 요약 정보"를 내려줄 때 사용합니다.
 *
 * @Getter: 응답 시 JSON으로 직렬화할 때 getter가 사용됨.
 * @AllArgsConstructor: from() 정적 메서드에서 new FollowUserResponse(...) 생성 시 사용.
 */
@Getter
@AllArgsConstructor
public class FollowUserResponse {
    private Long userId;
    private String userName;
    private String profileImageUrl;
    private String introduction;

    /**
     *  User 엔티티를 응답 DTO로 변환하는 정적 팩토리 메서드.
     * userName은 "닉네임#태그" 형태로 조합하여 반환합니다.
     */
    public static FollowUserResponse from(User user){
        return new FollowUserResponse(
                user.getId(),
                user.getUsername()+"#"+user.getUserTag(),
                user.getProfileImage(),
                user.getIntroduction()
        );
    }
}

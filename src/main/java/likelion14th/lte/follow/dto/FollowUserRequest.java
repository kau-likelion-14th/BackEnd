package likelion14th.lte.follow.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 팔로우 추가·언팔로우 API의 요청 본문(Request Body)을 담는 DTO입니다.
 * DTO(Data Transfer Object)는 계층 간 데이터를 전달할 때 사용하는 객체를 말합니다.
 * 클라이언트가 JSON으로 { "toUserId": 5 }를 보내면, 이 클래스의 toUserId 필드에 바인딩됩니다.
 *
 * @Getter : 필드 값을 읽기 위한 getter 메서드를 자동 생성합니다.
 * @NoArgsConstructor : 인자 없는 생성자입니다. JSON을 이 클래스로 역직렬화할 때 필요합니다.
 */
@Getter
@NoArgsConstructor
public class FollowUserRequest {
    /** 팔로우·언팔로우 대상 유저의 ID입니다. */
    private Long toUserId;
}

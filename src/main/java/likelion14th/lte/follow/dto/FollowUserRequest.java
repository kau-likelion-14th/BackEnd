package likelion14th.lte.follow.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *  팔로우/언팔로우 API 요청 바디용 DTO.
 * 클라이언트가 "누구를 팔로우할지/언팔로우할지"를 toUserId로 전달합니다.
 *
 * @Getter: Lombok - 필드 getter 자동 생성(JSON 직렬화·컨트롤러에서 getToUserId() 등).
 * @NoArgsConstructor: 인자 없는 생성자. JSON 역직렬화(요청 바디 → 객체) 시 필요.
 */
@Getter
@NoArgsConstructor
public class FollowUserRequest {
    /** 팔로우 대상 유저의 ID (요청 바디의 JSON 필드와 매핑됨) */
    private Long toUserId;
}

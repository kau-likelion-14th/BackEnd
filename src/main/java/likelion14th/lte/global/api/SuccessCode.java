package likelion14th.lte.global.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode implements BaseCode { // 성공
    OK(HttpStatus.OK, "COMMON_200", "Success"),
    CREATED(HttpStatus.CREATED, "COMMON_201", "Created"),

    USER_LOGIN_SUCCESS(HttpStatus.CREATED, "USER_2011", "회원가입& 로그인이 완료되었습니다."),
    USER_LOGOUT_SUCCESS(HttpStatus.OK, "USER_2001", "로그아웃 되었습니다."),
    USER_REISSUE_SUCCESS(HttpStatus.OK, "USER_2002", "토큰 재발급이 완료되었습니다."),
    USER_DELETE_SUCCESS(HttpStatus.OK, "USER_2003", "회원탈퇴가 완료되었습니다."),
    USER_PROFILE_UPDATE_SUCCESS(HttpStatus.OK, "USER_2006", "프로필 저장이 완료되었습니다."),
    USER_INFO_GET_SUCCESS(HttpStatus.OK, "USER_2007", "유저 정보 조회가 완료되었습니다."),

    FOLLOW_ADD_SUCCESS(HttpStatus.CREATED,"FOLLOW_2011","팔로우 추가가 완료되었습니다."),
    FOLLOW_DELETE_SUCCESS(HttpStatus.OK, "FOLLOW_2001", "언팔로우가 완료되었습니다."),
    FOLLOW_LIST_GET_SUCCESS(HttpStatus.OK, "FOLLOW_2002", "팔로우 목록 조회가 완료되었습니다."),
    FOLLOW_SEARCH_SUCCESS(HttpStatus.OK, "FOLLOW_2003", "팔로우 가능한 유저 검색이 완료되었습니다."),

    // 형이 추가한 코드
    MAJORSYNC_BULK_SUCCESS(HttpStatus.OK, "Sync_2012", "DB 동기화가 완료되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    // 응답 코드 상세 정보 return
    @Override
    public ReasonDTO getReason() {
        return ReasonDTO.builder()
                .httpStatus(this.httpStatus)
                .code(this.code)
                .message(this.message)
                .build();
    }
}

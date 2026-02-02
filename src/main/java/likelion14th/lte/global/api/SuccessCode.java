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

    // 형이 추가한 코드
    MAJORSYNC_BULK_SUCCESS(HttpStatus.OK, "Sync_2012", "DB 동기화가 완료되었습니다."),

    // Category
    CATEGORY_LIST_GET_SUCCESS(HttpStatus.OK, "CATEGORY_2001", "카테고리 목록 조회가 완료되었습니다."),

    // 투두
    TODO_LIST_GET_SUCCESS(HttpStatus.OK, "TODO_2001", "투두 목록 조회가 완료되었습니다."),
    TODO_DETAIL_GET_SUCCESS(HttpStatus.OK, "TODO_2002", "투두 상세 조회가 완료되었습니다."),
    TODO_CREATE_SUCCESS(HttpStatus.CREATED, "TODO_2011", "투두 생성이 완료되었습니다."),
    TODO_CALENDAR_MONTH_GET_SUCCESS(HttpStatus.OK, "TODO_2003", "월별 캘린더 조회가 완료되었습니다."),
    TODO_COMPLETE_SUCCESS(HttpStatus.OK, "TODO_2004", "투두 완료 상태 변경이 완료되었습니다."),
    TODO_DELETE_SUCCESS(HttpStatus.OK, "TODO_2005", "투두 삭제가 완료되었습니다."),
    ;

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

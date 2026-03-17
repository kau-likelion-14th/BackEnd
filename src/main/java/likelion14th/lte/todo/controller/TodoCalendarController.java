package likelion14th.lte.todo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import likelion14th.lte.global.api.ApiResponse;
import likelion14th.lte.global.api.SuccessCode;
import likelion14th.lte.todo.dto.response.TodoCalendarMonthResponse;
import likelion14th.lte.todo.service.TodoCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/todos")
@Tag(name = "캘린더 관리", description = "캘린더 조회")
@RequiredArgsConstructor
public class TodoCalendarController {

    private final TodoCalendarService todoCalendarService;

    /** 월별 캘린더 집계 **/
    @GetMapping("/calendar")
    @Operation(summary = "월별 캘린더 조회", description = "해당 월의 각 날짜별 남은 투두 개수를 반환합니다.")
    public ApiResponse<TodoCalendarMonthResponse> getCalendarMonth(
            @RequestParam Long userId,
            @RequestParam int year,
            @RequestParam int month
    ) {
        TodoCalendarMonthResponse response =
                todoCalendarService.getMonthRemainingCounts(userId, year, month);
        return ApiResponse.onSuccess(SuccessCode.TODO_CALENDAR_MONTH_GET_SUCCESS, response);
    }
}

package likelion14th.lte.todo.service;

import org.springframework.transaction.annotation.Transactional;
import likelion14th.lte.global.api.ErrorCode;
import likelion14th.lte.global.exception.GeneralException;
import likelion14th.lte.todo.domain.TodoDate;
import likelion14th.lte.todo.dto.response.TodoCalendarMonthResponse;
import likelion14th.lte.todo.repository.TodoDateRepository;
import likelion14th.lte.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TodoCalenderService {

    private final UserRepository userRepository;
    private final TodoDateRepository todoDateRepository;

    /** 월별 캘린더: 날짜별 남은 투두 개수 **/
    @Transactional(readOnly = true)
    public TodoCalendarMonthResponse getMonthRemainingCounts(Long userId, int year, int month) {

        // 사용자 검증
        userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        // 해당 월의 미완료 TodoDate 조회
        List<TodoDate> uncompleted = todoDateRepository
                .findAllByTodo_Category_User_IdAndDateBetweenAndCompletedFalse(userId, start, end);

        // date 기준으로 count
        Map<LocalDate, Long> counted = uncompleted.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        TodoDate::getDate,
                        java.util.stream.Collectors.counting()
                ));

        // 월 전체 날짜 0 -> 있는 날짜만 덮어쓰기
        Map<LocalDate, Long> result = new LinkedHashMap<>();
        for (int day = 1; day <= start.lengthOfMonth(); day++) {
            LocalDate mday = start.withDayOfMonth(day);
            result.put(mday, counted.getOrDefault(mday, 0L));
        }

        return new TodoCalendarMonthResponse(result);
    }
}

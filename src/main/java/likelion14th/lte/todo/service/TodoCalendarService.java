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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TodoCalendarService {

    private final UserRepository userRepository;
    private final TodoDateRepository todoDateRepository;

    /**
     * 월별 캘린더: 날짜별 남은 투두 개수
     **/
    @Transactional(readOnly = true)
    public TodoCalendarMonthResponse getMonthRemainingCounts(Long userId, int year, int month) {

        // 사용자 검증
        userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        // 월 범위 계산
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        // 해당 월의 모든 todoDate 조회
        List<TodoDate> all = todoDateRepository
                .findAllByTodo_User_IdAndDateBetween(userId, start, end);

        // 날짜별 total (그날 투두가 존재하는지 판단용)
        Map<LocalDate, Long> totalByDate = all.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        TodoDate::getDate,
                        java.util.stream.Collectors.counting()
                ));

        // 날짜별 remaining (미완료 개수가 0 초과인 날만 들어감)
        Map<LocalDate, Long> remainingByDate = all.stream()
                .filter(td -> !td.isCompleted())
                .collect(java.util.stream.Collectors.groupingBy(
                        TodoDate::getDate,
                        java.util.stream.Collectors.counting()
                ));

        List<TodoCalendarMonthResponse.DayInfo> days = new ArrayList<>();

        for (int day = 1; day <= start.lengthOfMonth(); day++) {
            LocalDate date = start.withDayOfMonth(day);
            boolean hasTodo = totalByDate.containsKey(date); // 투두가 있는가

            long remaining = hasTodo ? remainingByDate.getOrDefault(date, 0L) : 0L;

            days.add(new TodoCalendarMonthResponse.DayInfo(date, remaining, hasTodo));
        }

        return new TodoCalendarMonthResponse(days);
    }
}

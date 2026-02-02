package likelion14th.lte.todo.service;

import jakarta.transaction.Transactional;
import likelion14th.lte.global.api.ErrorCode;
import likelion14th.lte.global.exception.GeneralException;
import likelion14th.lte.todo.dto.response.TodoCalendarMonthResponse;
import likelion14th.lte.todo.repository.TodoDateRepository;
import likelion14th.lte.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TodoCalenderService {

    private final UserRepository userRepository;
    private final TodoDateRepository todoDateRepository;

    /**
     * 월별 캘린더: 날짜별 남은 투두 개수
     **/
    public TodoCalendarMonthResponse getMonthRemainingCounts(Long userId, int year, int month) {

        // 사용자 검증
        userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        Map<LocalDate, Long> result = new HashMap<>();
        for (int d = 1; d <= start.lengthOfMonth(); d++) {
            result.put(start.withDayOfMonth(d), 0L);
        }

        List<Object[]> rows = todoDateRepository.countUncompletedByDate(userId, start, end);

        for (Object[] row : rows) {
            LocalDate date = (LocalDate) row[0];
            Long count = (Long) row[1];
            result.put(date, count);
        }

        return new TodoCalendarMonthResponse(result);
    }
}

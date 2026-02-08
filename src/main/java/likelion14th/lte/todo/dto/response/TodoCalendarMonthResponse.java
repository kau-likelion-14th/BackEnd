package likelion14th.lte.todo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Map;

@Getter
@AllArgsConstructor
public class TodoCalendarMonthResponse {
    // key: yyyy-MM-dd, value: 남은 투두 개수
    private Map<LocalDate, Long> remainingCountByDate;
}

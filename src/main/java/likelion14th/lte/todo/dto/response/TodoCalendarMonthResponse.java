package likelion14th.lte.todo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class TodoCalendarMonthResponse {
    private List<DayInfo> days;

    @Getter
    @AllArgsConstructor
    public static class DayInfo {
        private LocalDate date;
        private long remaining;
        private boolean hasTodo;
    }
}

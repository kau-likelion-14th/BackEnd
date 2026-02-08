package likelion14th.lte.todo.generator;

import likelion14th.lte.todo.domain.Todo;
import likelion14th.lte.todo.domain.TodoDate;
import likelion14th.lte.todo.repository.TodoDateRepository;
import likelion14th.lte.todo.repository.TodoWeekRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import likelion14th.lte.todo.domain.TodoWeek;
import likelion14th.lte.todo.domain.WeekEnum;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoutineTodoDateGenerator {

    private final TodoWeekRepository todoWeekRepository;
    private final TodoDateRepository todoDateRepository;

    public void generate(Todo todo, LocalDate startDate, LocalDate endDate, LocalDate minStart) {
        if (startDate == null || endDate == null){
            log.warn("Routine generate skipped: start/end null. todoId={}", todo.getId());
            return;
        }
        // 시작일
        LocalDate start = startDate.isAfter(minStart) ? startDate : minStart;
        // 종료일 ( 1년 후/설정한 종료일 )
        LocalDate endLimit = start.plusYears(1).minusDays(1);
        LocalDate end = endDate.isBefore(endLimit) ? endDate : endLimit;

        if (start.isAfter(end)){
            log.warn("Routine generate skipped: start isAfter end. todoId={}", todo.getId());
            return;
        }

        // -> DayOfWeek 비교 위해
        Set<DayOfWeek> allowedDays =
                todoWeekRepository.findAllByTodo_Id(todo.getId())
                        .stream()
                        .map(TodoWeek::getWeek)// -> enum
                        .map(WeekEnum::toDayOfWeek)//->DayOfWeek
                        .collect(Collectors.toSet());

        if (allowedDays.isEmpty()){
            log.warn("Routine generate skipped: allowedDays empty. todoId={}", todo.getId());
            return;
        }

        Set<LocalDate> existingDates =
                // start-end 사이에 있는
                todoDateRepository.findAllByTodo_IdAndDateBetween(todo.getId(), start, end)
                        .stream()
                        .map(TodoDate::getDate)
                        .collect(Collectors.toSet());

        List<TodoDate> toCreate = new ArrayList<>();
        for (LocalDate day = start; !day.isAfter(end); day = day.plusDays(1)) {
            // 루틴 아닌 요일 스킵
            if (!allowedDays.contains(day.getDayOfWeek())) continue;
            // 이미 만들어진 날짜 스킵
            if (existingDates.contains(day)) continue;
            toCreate.add(TodoDate.create(todo, day));
        }
        if (!toCreate.isEmpty()) {
            todoDateRepository.saveAll(toCreate);
        }
    }
}


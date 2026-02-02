package likelion14th.lte.todo.scheduler;

import jakarta.transaction.Transactional;
import likelion14th.lte.todo.domain.Todo;
import likelion14th.lte.todo.domain.TodoDate;
import likelion14th.lte.todo.domain.TodoWeek;
import likelion14th.lte.todo.domain.Week;
import likelion14th.lte.todo.repository.TodoDateRepository;
import likelion14th.lte.todo.repository.TodoRepository;
import likelion14th.lte.todo.repository.TodoWeekRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoutineTodoScheduler {

    private final TodoRepository todoRepository;
    private final TodoWeekRepository todoWeekRepository;
    private final TodoDateRepository todoDateRepository;

    /**
     * 매일 새벽 3시 실행
     * - 루틴 투두에 대해 "오늘 ~ 1년 후" TodoDate 미리 생성
     * - 이미 존재하는 날짜는 생성하지 않음 (멱등)
     * - DB 조회 최소화 + saveAll 사용
     */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void generateRoutineTodoDatesOptimized() {

        LocalDate today = LocalDate.now();
        LocalDate oneYearLater = today.plusYears(1);

        // 1. 루틴 투두 전체 조회
        List<Todo> routines = todoRepository.findAllByRoutineEnabledTrue();

        for (Todo todo : routines) {

            // 2. 생성 대상 날짜 범위 계산
            LocalDate start = todo.getStartDate().isAfter(today)
                    ? todo.getStartDate()
                    : today;

            LocalDate end = todo.getEndDate().isBefore(oneYearLater)
                    ? todo.getEndDate()
                    : oneYearLater;

            if (start.isAfter(end)) continue;

            // 3. 요일 규칙 조회 (MON, TUE ...)
            Set<DayOfWeek> allowedDays =
                    todoWeekRepository.findAllByTodo_Id(todo.getId())
                            .stream()
                            .map(TodoWeek::getWeek)
                            .map(this::toDayOfWeek)
                            .collect(Collectors.toSet());

            if (allowedDays.isEmpty()) continue;

            // 4. 이미 존재하는 TodoDate를 "한 번에" 조회
            Set<LocalDate> existingDates =
                    todoDateRepository
                            .findAllByTodo_IdAndDateBetween(todo.getId(), start, end)
                            .stream()
                            .map(TodoDate::getDate)
                            .collect(Collectors.toSet());

            // 5. 새로 생성해야 할 TodoDate만 모아서 리스트로
            List<TodoDate> toCreate = new ArrayList<>();

            for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {

                // 요일 규칙 안 맞으면 스킵
                if (!allowedDays.contains(d.getDayOfWeek())) continue;

                // 이미 존재하면 스킵
                if (existingDates.contains(d)) continue;

                toCreate.add(TodoDate.create(todo, d));
            }

            // 6. 필요한 것만 saveAll (없으면 DB 접근 없음)
            if (!toCreate.isEmpty()) {
                todoDateRepository.saveAll(toCreate);
            }
        }
    }

    private DayOfWeek toDayOfWeek(Week week) {
        return switch (week) {
            case MON -> DayOfWeek.MONDAY;
            case TUE -> DayOfWeek.TUESDAY;
            case WED -> DayOfWeek.WEDNESDAY;
            case THU -> DayOfWeek.THURSDAY;
            case FRI -> DayOfWeek.FRIDAY;
            case SAT -> DayOfWeek.SATURDAY;
            case SUN -> DayOfWeek.SUNDAY;
        };
    }
}

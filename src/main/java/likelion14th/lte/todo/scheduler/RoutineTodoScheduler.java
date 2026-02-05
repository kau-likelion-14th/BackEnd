package likelion14th.lte.todo.scheduler;

import likelion14th.lte.todo.generator.RoutineTodoDateGenerator;
import org.springframework.transaction.annotation.Transactional;
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
    private final RoutineTodoDateGenerator routineTodoDateGenerator;

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void generateRoutineTodoDates() {
        LocalDate today = LocalDate.now();
        LocalDate oneYearLater = today.plusYears(1);

        List<Todo> routineTodos = todoRepository.findAllByRoutineEnabledTrue();

        for (Todo todo : routineTodos) {
            LocalDate start = todo.getStartDate().isAfter(today) ? todo.getStartDate() : today;
            LocalDate end = todo.getEndDate().isBefore(oneYearLater) ? todo.getEndDate() : oneYearLater;

            routineTodoDateGenerator.generate(todo, start, end);
        }
    }
}
package likelion14th.lte.todo.generator;

import likelion14th.lte.todo.domain.Todo;
import likelion14th.lte.todo.domain.TodoDate;
import likelion14th.lte.todo.repository.TodoDateRepository;
import likelion14th.lte.todo.repository.TodoWeekRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import likelion14th.lte.todo.domain.TodoWeek;
import likelion14th.lte.todo.domain.Week;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoutineTodoDateGenerator {

    private final TodoWeekRepository todoWeekRepository;
    private final TodoDateRepository todoDateRepository;

    public void generate(Todo todo, LocalDate start, LocalDate end) {
        if (start.isAfter(end)) return;

        Set<DayOfWeek> allowedDays =
                todoWeekRepository.findAllByTodo_Id(todo.getId())
                        .stream()
                        .map(TodoWeek::getWeek)
                        .map(Week::toDayOfWeek)
                        .collect(Collectors.toSet());

        if (allowedDays.isEmpty()) return;

        Set<LocalDate> existingDates =
                todoDateRepository.findAllByTodo_IdAndDateBetween(todo.getId(), start, end)
                        .stream()
                        .map(TodoDate::getDate)
                        .collect(Collectors.toSet());

        List<TodoDate> toCreate = new ArrayList<>();

        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            if (!allowedDays.contains(d.getDayOfWeek())) continue;
            if (existingDates.contains(d)) continue;
            toCreate.add(TodoDate.create(todo, d));
        }

        if (!toCreate.isEmpty()) {
            todoDateRepository.saveAll(toCreate);
        }
    }
}


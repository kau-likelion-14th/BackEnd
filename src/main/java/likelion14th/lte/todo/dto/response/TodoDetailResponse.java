package likelion14th.lte.todo.dto.response;

import likelion14th.lte.todo.domain.Todo;
import likelion14th.lte.todo.domain.TodoWeek;
import likelion14th.lte.todo.domain.WeekEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class TodoDetailResponse {

    private Long todoId;
    private String description;
    private String categoryName;

    private boolean routineEnabled;
    private LocalDate startDate;
    private LocalDate endDate;
    private Set<WeekEnum> week;


    public static TodoDetailResponse from(
            Todo todo,
            List<TodoWeek> todoWeeks
    ) {
        // Set 으로 변환하는 과정
        Set<WeekEnum> weeks = todoWeeks.stream()
                .map(TodoWeek::getWeek)
                .collect(Collectors.toSet());

        return new TodoDetailResponse(
                todo.getId(),
                todo.getDescription(),
                todo.getCategory().getCategoryName(),
                todo.isRoutineEnabled(),
                todo.getStartDate(),
                todo.getEndDate(),
                weeks
        );
    }

}


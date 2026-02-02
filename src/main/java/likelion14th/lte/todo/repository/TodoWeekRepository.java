package likelion14th.lte.todo.repository;

import likelion14th.lte.todo.domain.TodoWeek;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoWeekRepository extends JpaRepository<TodoWeek, Long> {
    List<TodoWeek> findAllByTodo_Id(Long todoId);
    void deleteAllByTodo_Id(Long todoId);
}

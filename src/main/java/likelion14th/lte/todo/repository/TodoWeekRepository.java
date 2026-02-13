package likelion14th.lte.todo.repository;

import likelion14th.lte.todo.domain.TodoWeek;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoWeekRepository extends JpaRepository<TodoWeek, Long> {
    // 루틴 투두의 요일 목록 조회
    List<TodoWeek> findAllByTodo_Id(Long todoId);
    // 삭제
    void deleteAllByTodo_Id(Long todoId);
}

package likelion14th.lte.todo.repository;

import likelion14th.lte.todo.domain.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    // 루틴 투두 전체 조회
    List<Todo> findAllByRoutineEnabledTrue();
}

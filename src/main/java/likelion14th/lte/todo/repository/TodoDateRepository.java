package likelion14th.lte.todo.repository;

import likelion14th.lte.todo.domain.TodoDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TodoDateRepository extends JpaRepository<TodoDate, Long> {

    Optional<TodoDate> findByTodo_IdAndDate(Long todoId, LocalDate date);
    List<TodoDate> findAllByTodo_Category_User_IdAndDate(Long userId, LocalDate date);
    // 특정 투두의 날짜 범위 TodoDate 전부 조회
    List<TodoDate> findAllByTodo_IdAndDateBetween(
            Long todoId,
            LocalDate start,
            LocalDate end
    );
    boolean existsByTodo_Id(Long todoId);

    void deleteAllByTodo_IdAndDateGreaterThanEqual(Long todoId, LocalDate from);

    List<TodoDate> findAllByTodo_Category_User_IdAndDateBetweenAndCompletedAtIsNull( Long userId, LocalDate start, LocalDate end);
}


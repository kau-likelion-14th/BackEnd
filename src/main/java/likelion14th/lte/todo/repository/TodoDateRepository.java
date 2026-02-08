package likelion14th.lte.todo.repository;

import likelion14th.lte.todo.domain.TodoDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TodoDateRepository extends JpaRepository<TodoDate, Long> {
    // 투두-날짜 조회
    Optional<TodoDate> findByTodo_IdAndDate(Long todoId, LocalDate date);
    // 유저의 특정 날짜 전체 투두 조회
    List<TodoDate> findAllByTodo_Category_User_IdAndDate(Long userId, LocalDate date);
    // 특정 투두의 날짜 범위 TodoDate 전부 조회
    List<TodoDate> findAllByTodo_IdAndDateBetween( Long todoId,  LocalDate start,  LocalDate end );
    // 이 투두가 가진 날짜의 존재 확인 (삭제 판단)
    boolean existsByTodo_Id(Long todoId);
    // 과거는 남기고 미래만 삭제
    void deleteAllByTodo_IdAndDateGreaterThanEqual(Long todoId, LocalDate from);
    // userId + 날짜 범위 + 미완료(completed = false) TodoDate 전부 조회
    List<TodoDate> findAllByTodo_Category_User_IdAndDateBetweenAndCompletedFalse( Long userId, LocalDate start, LocalDate end);
}


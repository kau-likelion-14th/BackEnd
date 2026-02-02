package likelion14th.lte.todo.repository;

import likelion14th.lte.todo.domain.TodoDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TodoDateRepository extends JpaRepository<TodoDate, Long> {

    Optional<TodoDate> findByTodo_IdAndDate(Long todoId, LocalDate date);

    // 날짜별 완료 상태를 한번에 가져오고 싶으면 이거까지 추가
    List<TodoDate> findAllByTodo_IdInAndDate(List<Long> todoIds, LocalDate date);

    void deleteAllByTodo_Id(Long todoId);

    List<TodoDate> findAllByTodo_Category_User_IdAndDate(Long userId, LocalDate date);

    List<Object[]> countUncompletedByDate(
            Long userId,
            LocalDate start,
            LocalDate end
    );

    // 특정 투두의 날짜 범위 TodoDate 전부 조회
    List<TodoDate> findAllByTodo_IdAndDateBetween(
            Long todoId,
            LocalDate start,
            LocalDate end
    );
}


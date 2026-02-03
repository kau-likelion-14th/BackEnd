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


    @Query("""
    select td.date, count(td)
    from TodoDate td
    where td.todo.category.user.id = :userId
      and td.date between :start and :end
      and td.completed = false
    group by td.date
    """)
    List<Object[]> countUncompletedByDate(
            @Param("userId") Long userId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );
}


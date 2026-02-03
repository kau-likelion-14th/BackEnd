package likelion14th.lte.todo.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(// 중복 불가 제약 조건
        uniqueConstraints = {@UniqueConstraint(name = "uk_todo_date", columnNames = {"todo_id", "date"})})
public class TodoDate{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    private LocalDateTime completedAt;

    @Column(nullable = false)
    private boolean completed = false;

    /** 연관관계 **/
    // 투두와 일대다
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id", nullable = false)
    private Todo todo;

    /** 생성 메서드 **/
    public static TodoDate create(Todo todo, LocalDate date) {
        TodoDate todoDate = new TodoDate();
        todoDate.todo = todo;
        todoDate.date = date;
        todoDate.completed = false;
        todoDate.completedAt = null;
        return todoDate;
    }

    /** 비즈니스 로직 등 **/
    public void toggleCompleted() {
        this.completed = !this.completed;
        this.completedAt = this.completed ? LocalDateTime.now() : null;
    }
}

package likelion14th.lte.todo.domain;

import jakarta.persistence.*;
import likelion14th.lte.Entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TodoWeek extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id", nullable = false)
    private Todo todo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WeekEnum week;

    public static TodoWeek of(Todo todo, WeekEnum week) {
        TodoWeek todoWeek = new TodoWeek();
        todoWeek.todo = todo;
        todoWeek.week = week;
        return todoWeek;
    }
}

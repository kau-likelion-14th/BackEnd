package likelion14th.lte.todo.domain;

import jakarta.persistence.*;
import likelion14th.lte.Entity.BaseEntity;
import likelion14th.lte.category.domain.Category;
import likelion14th.lte.user.domain.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Todo extends BaseEntity {

    /** 필드 **/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private boolean routineEnabled;

    private LocalDate startDate;
    private LocalDate endDate;

    /** 연관관계 **/
    // 카테고리 일대다
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy="todo", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<TodoDate> todoDates;

    @OneToMany(mappedBy="todo", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<TodoWeek> todoWeeks;

    /** 양방향 메서드 **/

    /** 생성자 및 비즈니스 로직 **/
    public static Todo create(User user, String description, Category category,
                              boolean routineEnabled, LocalDate startDate, LocalDate endDate) {
        Todo todo = new Todo();
        todo.user = user;
        todo.description = description;
        todo.category = category;
        todo.routineEnabled = routineEnabled;
        todo.startDate = startDate;
        todo.endDate = endDate;
        return todo;
    }

    public void update(String description, Category category,
                       boolean routineEnabled, LocalDate startDate, LocalDate endDate) {
        this.description = description;
        this.category = category;
        this.routineEnabled = routineEnabled;
        this.startDate = startDate;
        this.endDate = endDate;
    }


}

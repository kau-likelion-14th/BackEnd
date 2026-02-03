package likelion14th.lte.todo.domain;

import jakarta.persistence.*;
import likelion14th.lte.category.domain.Category;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class Todo {

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

    /** 양방향 메서드 **/

    /** 생성자 및 비즈니스 로직 **/
    public static Todo create(String description, Category category,
                              boolean routineEnabled, LocalDate startDate, LocalDate endDate) {
        Todo todo = new Todo();
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

package likelion14th.lte.statistic.domain;

import jakarta.persistence.*;
import likelion14th.lte.Entity.BaseEntity;
import likelion14th.lte.todo.domain.WeekEnum;
import likelion14th.lte.user.domain.User;
import lombok.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "statistic")
public class Statistic extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "statistic_id")
    @Getter(AccessLevel.PRIVATE)
    private Long id;

    @Column(nullable = false)
    private int streak=0;

    @Column(nullable = false)
    private int monthPercent=0;

    @OneToMany(
            mappedBy = "statistic",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<StatWeek> statWeeks = new ArrayList<>();

    @OneToOne(mappedBy = "statistic")
    private User user;

    public static Statistic create() {
        Statistic statistic = new Statistic();
        statistic.initializeWeeks();
        return statistic;
    }
    private void initializeWeeks() {
        for (WeekEnum week : WeekEnum.values()) {
            StatWeek statWeek = StatWeek.builder()
                    .week(week)
                    .count(0)
                    .statistic(this)
                    .build();
            this.statWeeks.add(statWeek);
        }
    }
    public WeekEnum getMaxWeek() {
        return statWeeks.stream()
                .max(Comparator.comparingInt(StatWeek::getCount))
                .map(StatWeek::getWeek)
                .orElse(null);
    }

}

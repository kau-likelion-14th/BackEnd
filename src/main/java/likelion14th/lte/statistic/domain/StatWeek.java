package likelion14th.lte.statistic.domain;


import jakarta.persistence.*;
import lombok.*;

import likelion14th.lte.todo.domain.WeekEnum;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StatWeek {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WeekEnum week;

    @Column(nullable = false)
    private int count;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Statistic statistic;

    @Builder
    private StatWeek(WeekEnum week, int count, Statistic statistic) {
        this.week = week;
        this.count = count;
        this.statistic = statistic;
    }

    public void increase(){
        this.count++;
    }


}

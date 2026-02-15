package likelion14th.lte.statistic.dto;

import likelion14th.lte.statistic.domain.Statistic;
import likelion14th.lte.todo.domain.WeekEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class StatisticResponse {
    private int streak;
    private int monthPercent;
    private WeekEnum mostTodoWeek;

    public static StatisticResponse from(Statistic statistic) {
        return  new StatisticResponse (
                statistic.getStreak(),
                statistic.getMonthPercent(),
                statistic.getMaxWeek()
        );
    }
}

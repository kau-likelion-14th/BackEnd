package likelion14th.lte.statistic.service;

import jakarta.persistence.EntityManager;
import likelion14th.lte.global.api.ErrorCode;
import likelion14th.lte.global.exception.GeneralException;
import likelion14th.lte.statistic.domain.Statistic;
import likelion14th.lte.todo.repository.TodoDateRepository;
import likelion14th.lte.user.domain.User;
import likelion14th.lte.user.repository.UserRepository;
import likelion14th.lte.statistic.dto.StatisticResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class StatisticService {

    private final UserRepository userRepository;

    private final TodoDateRepository todoDateRepository;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public StatisticResponse getStatistic(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        return StatisticResponse.from(user.getStatistic());
    }


    @Transactional
    public void updateAllStatistics() {
        int page = 0;
        int size = 500;

        Page<User> userPage;

        do {
            userPage = userRepository.findAll(PageRequest.of(page, size));

            for (User user : userPage.getContent()) {
                updateStatistic(user);
            }
            entityManager.flush();
            entityManager.clear();

            page++;
        } while (userPage.hasNext());
    }



    private void updateStatistic(User user) {
        LocalDate day = LocalDate.now().minusDays(1);
        LocalDate before30Days = day.minusDays(30);

        Statistic statistic = user.getStatistic();

        boolean existsUsersSuccessTodo =
                todoDateRepository.existsByTodo_User_IdAndDateAndCompleted(
                    user.getId(),day,true
                );
        boolean existsUsersFailureTodo  =
                todoDateRepository.existsByTodo_User_IdAndDateAndCompleted(
                        user.getId(),day,false
                );


        if(existsUsersSuccessTodo && !existsUsersFailureTodo) {
            statistic.increaseStreakIfSuccess(true);
            statistic.getStatWeeks().stream()
                    .filter(w->w.getWeek().toDayOfWeek()==day.getDayOfWeek())
                    .findFirst()
                    .orElseThrow(()->new GeneralException(ErrorCode.TODO_ROUTINE_DAY_OF_WEEK_INVALID))
                    .increase();
        } else {
            statistic.increaseStreakIfSuccess(false);
        }


        int completedTodo30DayCnt= todoDateRepository.countByTodo_User_IdAndDateBetweenAndCompleted(
                user.getId(), before30Days, day, true
        );
        int failureTodo30DayCnt = todoDateRepository.countByTodo_User_IdAndDateBetweenAndCompleted(
                user.getId(),before30Days,day,false
        );

        int sumCnt = completedTodo30DayCnt+failureTodo30DayCnt;

        if(sumCnt<=0) {
            statistic.setMonthPercent(0);
        } else{
            statistic.setMonthPercent((completedTodo30DayCnt*100)/sumCnt);
        }

    }

}

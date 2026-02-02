package likelion14th.lte.todo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import likelion14th.lte.todo.domain.Week;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@AllArgsConstructor
public class TodoCreateRequest {

    @NotBlank(message = "카테고리 선택은 필수입니다.")
    private String categoryName;

    @NotBlank(message = "내용 입력은 필수입니다.")
    private String description;

    // 프론트에서 루틴 팝업 창의 [저장]을 누르면 true
    private boolean routineEnabled;

    // 일반 투두는 이 날짜에 1회 생성 (TodoDate)
    private LocalDate date;

    private LocalDate startDate;

    private LocalDate endDate;

    // 혹시나 하는 중복 제거
    private Set<Week> week;
}

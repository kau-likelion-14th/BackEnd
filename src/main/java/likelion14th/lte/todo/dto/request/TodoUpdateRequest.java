package likelion14th.lte.todo.dto.request;

import jakarta.validation.constraints.NotBlank;
import likelion14th.lte.todo.domain.Week;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@AllArgsConstructor
public class TodoUpdateRequest {

    @NotBlank(message = "카테고리 선택은 필수입니다.")
    private String categoryName;

    @NotBlank(message = "내용 입력은 필수입니다.")
    private String description;

    private boolean routineEnabled;

    private LocalDate startDate;
    private LocalDate endDate;

    private Set<Week> week; // 루틴일 때 필수
}


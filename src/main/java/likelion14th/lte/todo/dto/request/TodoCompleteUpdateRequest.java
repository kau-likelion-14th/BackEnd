package likelion14th.lte.todo.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TodoCompleteUpdateRequest {

    @NotNull
    private Boolean completed;
}

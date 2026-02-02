package likelion14th.lte.todo.dto.response;

import likelion14th.lte.todo.domain.Todo;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** 투두리스트를 위한 Dto **/
@Getter
@AllArgsConstructor
public class TodoListResponse {

    /** 필요한 것 : id, 설명,카테고리, 완료 여부**/
    private Long todoId;
    private String description;
    private String CategoryName;
    private boolean isCompleted;

    public static TodoListResponse from(Todo todo, boolean isCompleted){
        return new TodoListResponse(
                todo.getId(),
                todo.getDescription(),
                todo.getCategory().getCategoryName(),
                isCompleted
        );
    }

    public static TodoListResponse of(Todo todo, boolean completed) {
        return new TodoListResponse(
                todo.getId(),
                todo.getDescription(),
                todo.getCategory().getCategoryName(),
                completed
        );
    }
}


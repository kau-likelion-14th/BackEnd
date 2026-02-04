package likelion14th.lte.todo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import likelion14th.lte.global.api.ApiResponse;
import likelion14th.lte.global.api.SuccessCode;
import likelion14th.lte.todo.dto.request.TodoCreateRequest;
import likelion14th.lte.todo.dto.request.TodoUpdateRequest;
import likelion14th.lte.todo.dto.response.TodoDetailResponse;
import likelion14th.lte.todo.dto.response.TodoListResponse;
import likelion14th.lte.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/todos")
@Tag(name = "투두 관리", description = "투두 관련 api")
@RequiredArgsConstructor
public class TodoController {
    private final TodoService todoService;

    /** 투두 리스트 조회 **/
    @GetMapping
    @Operation(summary = "투두 리스트 조회", description = "선택한 날짜의 투두리스트를 조회합니다.")
    public ApiResponse<List<TodoListResponse>> getTodosByDate(
            @AuthenticationPrincipal Long userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date
    ){
        List<TodoListResponse> todos = todoService.getTodosByDate(userId, date);
        return ApiResponse.onSuccess(SuccessCode.TODO_LIST_GET_SUCCESS, todos);
    }

    /** 투두 상세 조회 **/
    @GetMapping("/{todoId}")
    @Operation(summary = "투두 상세 조회", description = "투두의 상세 정보를 조회합니다.")
    public ApiResponse<TodoDetailResponse> getTodoDetail(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long todoId
    ){
        TodoDetailResponse todo = todoService.getTodoDetail(userId, todoId);
        return ApiResponse.onSuccess(SuccessCode.TODO_DETAIL_GET_SUCCESS, todo);
    }

    /** 투두 상세 수정 **/
    @PutMapping("/{todoId}")
    @Operation(summary = "투두 상세 수정", description = "투두의 상세 정보를 수정합니다.")
    public ApiResponse<TodoDetailResponse> updateTodoDetail(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long todoId,
            @RequestBody @Valid TodoUpdateRequest todoUpdateRequest
    ){
        TodoDetailResponse updatedResponse = todoService.updateTodoDetail(
                userId,
                todoId,
                todoUpdateRequest
        );
        return ApiResponse.onSuccess(SuccessCode.TODO_DETAIL_UPDATE_SUCCESS, updatedResponse);
    }

    /** 투두 추가 **/
    @PostMapping
    @Operation(summary = "투두 추가", description = "투두를 추가합니다.")
    public ApiResponse<TodoDetailResponse> createTodo(
            @AuthenticationPrincipal Long userId,
            @RequestBody @Valid TodoCreateRequest todoCreateRequest,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date
            ){
        TodoDetailResponse createdResponse = todoService.createTodo(userId,todoCreateRequest, date);
        return ApiResponse.onSuccess(SuccessCode.TODO_CREATE_SUCCESS, createdResponse);
    }

    /** 투두 삭제 **/
    @DeleteMapping("/{todoId}/dates/{date}")
    @Operation(summary = "투두 삭제", description = "투두를 삭제합니다.")
    public ApiResponse<String> deleteTodo(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long todoId,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date
    ){
        todoService.deleteTodo(userId, todoId, date);
        return ApiResponse.onSuccess(SuccessCode.TODO_DELETE_SUCCESS, "OK");
    }

    /** 투두 완료 처리 **/
    @PatchMapping("/{todoId}/dates/{date}/complete")
    @Operation(summary = "투두 완료 토글", description = "선택한 날짜의 투두 완료/해제를 토글합니다.")
    public ApiResponse<TodoListResponse> updateTodoComplete(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long todoId,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date
    ) {
        TodoListResponse updatedResponse = todoService.todoComplete(userId, todoId, date);
        return ApiResponse.onSuccess(SuccessCode.TODO_COMPLETE_SUCCESS, updatedResponse);
    }
}

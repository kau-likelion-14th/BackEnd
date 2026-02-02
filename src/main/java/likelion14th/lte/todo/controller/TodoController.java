package likelion14th.lte.todo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import likelion14th.lte.global.api.ApiResponse;
import likelion14th.lte.global.api.SuccessCode;
import likelion14th.lte.todo.dto.request.TodoCreateRequest;
import likelion14th.lte.todo.dto.request.TodoUpdateRequest;
import likelion14th.lte.todo.dto.response.TodoDetailResponse;
import likelion14th.lte.todo.dto.response.TodoListResponse;
import likelion14th.lte.todo.dto.TodoResponse;
import likelion14th.lte.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
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
    @Operation(summary = "투두 리스트 조회", description = "홈에서 보여줄, 로그인한 사용자의 투두리스트를 조회합니다.")
    public ApiResponse<List<TodoListResponse>> getAllTodos(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date
    ){
        List<TodoListResponse> todos = todoService.getTodosByDate(customUserDetails.getUserId(), date);
        return ApiResponse.onSuccess(SuccessCode.TODO_LIST_GET_SUCCESS, todos);
    }

    /** 투두 상세 조회 **/
    @GetMapping("/{todoId}")
    @Operation(summary = "투두 상세 조회", description = "투두의 상세 정보를 조회합니다.")
    public ApiResponse<TodoDetailResponse> getTodoDetail(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long todoId
    ){
        TodoDetailResponse todo = todoService.getTodoDetail(customUserDetails.getUserId(), todoId);
        return ApiResponse.onSuccess(SuccessCode.TODO_DETAIL_GET_SUCCESS, todo);
    }

    /** 투두 상세 수정 **/
    @PutMapping("/{todoId}")
    @Operation(summary = "투두 상세 수정", description = "투두의 상세 정보를 수정합니다.")
    public ApiResponse<TodoDetailResponse> updateTodoDetail(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long todoId,
            @RequestBody TodoUpdateRequest todoUpdateRequest
    ){
        TodoDetailResponse todo = todoService.updateTodoDetail(
                customUserDetails.getUserId(),
                todoId,
                todoUpdateRequest
        );
        return ApiResponse.onSuccess(SuccessCode.TODO_DETAIL_UPDATE_SUCCESS, todo);
    }

    /** 투두 추가 **/
    @PostMapping
    @Operation(summary = "투두 추가", description = "투두를 추가합니다.")
    public ApiResponse<TodoListResponse> createTodo(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody TodoCreateRequest todoCreateRequest,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date
            ){
        TodoDetailResponse created = todoService.createTodo(customUserDetails.getUserId(),todoCreateRequest, date);
        return ApiResponse.onSuccess(SuccessCode.TODO_CREATE_SUCCESS, created);
    }

    /** 투두 삭제 **/
    @DeleteMapping("/{todoId}")
    @Operation(summary = "투두 삭제", description = "투두를 삭제합니다.")
    public ApiResponse<String> deleteTodo(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long todoId
    ){
        todoService.deleteTodo(customUserDetails.getUserId(), todoId);
        return ApiResponse.onSuccess(SuccessCode.TODO_DELETE_SUCCESS, "OK");
    }

    /** 투두 완료 처리 **/
    @PatchMapping("/{todoId}/dates/{date}/complete")
    @Operation(summary = "투두 완료 토글", description = "선택한 날짜의 투두 완료/해제를 토글합니다.")
    public ApiResponse<TodoListResponse> toggleTodoComplete(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long todoId,
            // yyyy-MM-dd 형식으로 파밍
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date
    ) {
        TodoListResponse updated = todoService.todoComplete(customUserDetails.getUserId(), todoId, date);
        return ApiResponse.onSuccess(SuccessCode.TODO_COMPLETE_SUCCESS, updated);
    }
}

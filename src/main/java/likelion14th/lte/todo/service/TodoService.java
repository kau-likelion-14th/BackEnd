package likelion14th.lte.todo.service;

import jakarta.transaction.Transactional;
import likelion14th.lte.category.domain.Category;
import likelion14th.lte.category.repository.CategoryRepository;
import likelion14th.lte.global.api.ErrorCode;
import likelion14th.lte.global.exception.GeneralException;
import likelion14th.lte.todo.domain.Todo;
import likelion14th.lte.todo.domain.TodoDate;
import likelion14th.lte.todo.domain.TodoWeek;
import likelion14th.lte.todo.dto.request.TodoCreateRequest;
import likelion14th.lte.todo.dto.request.TodoUpdateRequest;
import likelion14th.lte.todo.dto.response.TodoDetailResponse;
import likelion14th.lte.todo.dto.response.TodoListResponse;
import likelion14th.lte.todo.repository.TodoDateRepository;
import likelion14th.lte.todo.repository.TodoRepository;
import likelion14th.lte.todo.repository.TodoWeekRepository;
import likelion14th.lte.user.domain.User;
import likelion14th.lte.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final CategoryRepository categoryRepository;
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;
    private final TodoWeekRepository todoWeekRepository;
    private final TodoDateRepository todoDateRepository;

    /** 투두리스트 조회 **/
    @Transactional
    public List<TodoListResponse> getTodosByDate(Long userId, LocalDate date){
        // 사용자 검증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        // 유저 + 날짜 기준 TodoDate 조회
        List<TodoDate> todoDates =  todoDateRepository.findAllByTodo_Category_User_IdAndDate(userId, date);

        // Dto 변환
        return todoDates.stream()
                .map(td -> TodoListResponse.of(td.getTodo(), td.isCompleted()))
                .toList();
    }

    /** 투두 상세 조회 **/
    public TodoDetailResponse getTodoDetail(Long userId, Long todoId){
        // 사용자 검증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));
        // 투두 검증
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new GeneralException(ErrorCode.TODO_NOT_FOUND));

        // 투두 서유자 검증
        if (!todo.getCategory().getUser().getId().equals(user.getId())) {
            throw new GeneralException(ErrorCode.TODO_ACCESS_DENIED);
        }

        // todoWeek 조회
        List<TodoWeek> todoWeeks = List.of();
        if (todo.isRoutineEnabled()) {
            todoWeeks = todoWeekRepository.findAllByTodo_Id(todoId);
            // 루틴인데 week 가 하나도 없으면 데이터 이상 상태
            if (todoWeeks.isEmpty()) {
                throw new GeneralException(ErrorCode.TODO_ROUTINE_RULE_INCONSISTENT);
            }
        }

        return TodoDetailResponse.from(todo, todoWeeks);
    }

    /** 투두 상세 수정 **/
    public TodoDetailResponse updateTodoDetail(
            Long userId,
            Long todoId,
            TodoUpdateRequest request
    ){
        // 사용자 검증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));
        // 투두 조회
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new GeneralException(ErrorCode.TODO_NOT_FOUND));
        // 투두 소유자 검증
        if (!todo.getCategory().getUser().getId().equals(user.getId())) {
            throw new GeneralException(ErrorCode.TODO_ACCESS_DENIED);
        }
        // 카테고리 변경 검증/조회
        Category category = categoryRepository
                .findByUser_IdAndCategoryName(user.getId(), request.getCategoryName())
                .orElseThrow(() -> new GeneralException(ErrorCode.CATEGORY_NOT_FOUND));
        // 루틴 요청 값 파싱/검증
        boolean routineEnabled = request.isRoutineEnabled();
        LocalDate startDate = null;
        LocalDate endDate = null;

        if (routineEnabled) {
            startDate = (request.getStartDate() != null) ? request.getStartDate() : LocalDate.now();

            if (request.getEndDate() == null) {
                throw new GeneralException(ErrorCode.TODO_ROUTINE_END_DATE_REQUIRED);
            }
            endDate = request.getEndDate();

            if (startDate.isAfter(endDate)) {
                throw new GeneralException(ErrorCode.TODO_ROUTINE_DATE_RANGE_INVALID);
            }

            if (request.getWeek() == null || request.getWeek().isEmpty()) {
                throw new GeneralException(ErrorCode.TODO_ROUTINE_DAYS_REQUIRED);
            }
        }

        // 투두 엔티티 값 변경
        todo.update(
                request.getDescription(),
                category,
                routineEnabled,
                startDate,
                endDate
        );

        // TodoWeek 처리
        todoWeekRepository.deleteAllByTodo_Id(todoId);

        if (routineEnabled) {
            List<TodoWeek> todoWeeks = request.getWeek().stream()
                    .map(week -> TodoWeek.of(todo, week))
                    .collect(Collectors.toList());

            todoWeekRepository.saveAll(todoWeeks);

            return TodoDetailResponse.from(todo, todoWeeks);
        }

        return TodoDetailResponse.from(todo, List.of());
    }

    /** 투두 생성 **/
    @Transactional
    public TodoDetailResponse createTodo(
            Long userId,
            TodoCreateRequest request,
            LocalDate date
    ) {
        // 1. 사용자 검증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        // 2. 카테고리 검증
        Category category = categoryRepository
                .findByUser_IdAndCategoryName(user.getId(), request.getCategoryName())
                .orElseThrow(() -> new GeneralException(ErrorCode.CATEGORY_NOT_FOUND));

        boolean routineEnabled = request.isRoutineEnabled();

        LocalDate startDate = null;
        LocalDate endDate = null;

        // 3. 일반 투두 검증
        if (!routineEnabled) {
            if (date == null) {
                throw new GeneralException(ErrorCode.TODO_DATE_REQUIRED);
            }
        }

        // 4. 루틴 투두 검증
        if (routineEnabled) {
            startDate = (request.getStartDate() != null)
                    ? request.getStartDate()
                    : LocalDate.now();

            if (request.getEndDate() == null) {
                throw new GeneralException(ErrorCode.TODO_ROUTINE_END_DATE_REQUIRED);
            }

            endDate = request.getEndDate();

            if (startDate.isAfter(endDate)) {
                throw new GeneralException(ErrorCode.TODO_ROUTINE_DATE_RANGE_INVALID);
            }

            if (request.getWeek() == null || request.getWeek().isEmpty()) {
                throw new GeneralException(ErrorCode.TODO_ROUTINE_DAYS_REQUIRED);
            }
        }

        // 5. Todo 저장 (정의)
        Todo todo = Todo.create(
                request.getDescription(),
                category,
                routineEnabled,
                startDate,
                endDate
        );
        Todo savedTodo = todoRepository.save(todo);

        // 6. 일반 투두 → TodoDate 1개 생성
        if (!routineEnabled) {
            TodoDate todoDate = TodoDate.create(savedTodo, date);
            todoDateRepository.save(todoDate);
            return TodoDetailResponse.from(savedTodo, List.of());
        }

        // 7. 루틴 투두 → TodoWeek 저장
        List<TodoWeek> todoWeeks = request.getWeek().stream()
                .map(week -> TodoWeek.of(savedTodo, week))
                .toList();
        todoWeekRepository.saveAll(todoWeeks);

        return TodoDetailResponse.from(savedTodo, todoWeeks);
    }

    /** 투두 삭제 **/
    @Transactional
    public void deleteTodo(Long userId, Long todoId){
        // 사용자 검증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));
        // 투두 검증
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new GeneralException(ErrorCode.TODO_NOT_FOUND));
        // 소유자 검증
        if (!todo.getCategory().getUser().getId().equals(user.getId())) {
            throw new GeneralException(ErrorCode.TODO_ACCESS_DENIED);
        }
        // 루틴이면 -> todoWeek 삭제
        todoWeekRepository.deleteAllByTodo_Id(todoId);
        // 투두 삭제
        todoRepository.delete(todo);
    }

    @Transactional
    public TodoListResponse todoComplete(Long userId, Long todoId, LocalDate date) {

        // 사용자 검증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        // 투두 존재 검증
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new GeneralException(ErrorCode.TODO_NOT_FOUND));

        // 소유자 검증
        if (!todo.getCategory().getUser().getId().equals(user.getId())) {
            throw new GeneralException(ErrorCode.TODO_ACCESS_DENIED);
        }

        TodoDate todoDate = todoDateRepository.findByTodo_IdAndDate(todoId, date)
                .orElseThrow(() -> new GeneralException(ErrorCode.TODO_DATE_NOT_FOUND)); // 없으면 ErrorCode 추가 필요

        todoDate.toggleCompleted();

        return TodoListResponse.from(todo, todoDate.isCompleted());
    }

}

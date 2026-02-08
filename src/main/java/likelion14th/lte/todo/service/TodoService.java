package likelion14th.lte.todo.service;

import likelion14th.lte.category.domain.Category;
import likelion14th.lte.category.repository.CategoryRepository;
import likelion14th.lte.global.api.ErrorCode;
import likelion14th.lte.global.exception.GeneralException;
import likelion14th.lte.todo.domain.Todo;
import likelion14th.lte.todo.domain.TodoDate;
import likelion14th.lte.todo.domain.TodoWeek;
import likelion14th.lte.todo.domain.WeekEnum;
import likelion14th.lte.todo.dto.request.TodoCreateRequest;
import likelion14th.lte.todo.dto.request.TodoUpdateRequest;
import likelion14th.lte.todo.dto.response.TodoDetailResponse;
import likelion14th.lte.todo.dto.response.TodoListResponse;
import likelion14th.lte.todo.generator.RoutineTodoDateGenerator;
import likelion14th.lte.todo.repository.TodoDateRepository;
import likelion14th.lte.todo.repository.TodoRepository;
import likelion14th.lte.todo.repository.TodoWeekRepository;
import likelion14th.lte.user.domain.User;
import likelion14th.lte.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final CategoryRepository categoryRepository;
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;
    private final TodoWeekRepository todoWeekRepository;
    private final TodoDateRepository todoDateRepository;
    private final RoutineTodoDateGenerator routineTodoDateGenerator;

    /** 헬퍼 메서드 **/
    // 사용자 검증
    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));
    }
    // 투두 검증
    private Todo getTodoOrThrow(Long todoId) {
        return todoRepository.findById(todoId)
                .orElseThrow(() -> new GeneralException(ErrorCode.TODO_NOT_FOUND));
    }
    // 투두 소유자 검증
    private void assertOwner(User user, Todo todo) {
        if (!todo.getCategory().getUser().getId().equals(user.getId())) {
            throw new GeneralException(ErrorCode.TODO_ACCESS_DENIED);
        }
    }
    // 카테고리 검증
    private Category getCategoryOrThrow(Long userId, String categoryName) {
        return categoryRepository.findByUser_IdAndCategoryName(userId, categoryName)
                .orElseThrow(() -> new GeneralException(ErrorCode.CATEGORY_NOT_FOUND));
    }
    // 루틴 투두 검증
    private record RoutineInfo(LocalDate startDate, LocalDate endDate) {}
    private RoutineInfo validateRoutineOrThrow(
            boolean routineEnabled,
            LocalDate reqStartDate,
            LocalDate reqEndDate,
            Set<WeekEnum> reqWeek
    ) {
        if (!routineEnabled) {
            return new RoutineInfo(null, null);
        }
        LocalDate startDate = (reqStartDate != null) ? reqStartDate : LocalDate.now();

        if (reqEndDate == null) {
            throw new GeneralException(ErrorCode.TODO_ROUTINE_END_DATE_REQUIRED);
        }
        LocalDate endDate = reqEndDate;

        if (startDate.isAfter(endDate)) {
            throw new GeneralException(ErrorCode.TODO_ROUTINE_DATE_RANGE_INVALID);
        }

        if (reqWeek == null || reqWeek.isEmpty()) {
            throw new GeneralException(ErrorCode.TODO_ROUTINE_WEEK_REQUIRED);
        }

        return new RoutineInfo(startDate, endDate);
    }

    /** 투두리스트 조회 **/
    @Transactional(readOnly = true)
    public List<TodoListResponse> getTodosByDate(Long userId, LocalDate date){
        // 사용자 검증
        User user = getUserOrThrow(userId);
        // 유저 + 날짜 기준 TodoDate 조회
        List<TodoDate> todoDates =  todoDateRepository.findAllByTodo_Category_User_IdAndDate(user.getId(), date);
        // Dto 변환
        return todoDates.stream()
                .map(td -> TodoListResponse.from(td.getTodo(), td.isCompleted()))
                .toList();
    }

    /** 투두 상세 조회 **/
    @Transactional(readOnly = true)
    public TodoDetailResponse getTodoDetail(Long userId, Long todoId){
        User user = getUserOrThrow(userId);
        Todo todo = getTodoOrThrow(todoId);
        assertOwner(user, todo);

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
    @Transactional
    public TodoDetailResponse updateTodoDetail(
            Long userId,
            Long todoId,
            TodoUpdateRequest request
    ){
        User user = getUserOrThrow(userId);
        Todo todo = getTodoOrThrow(todoId);
        assertOwner(user, todo);

        boolean wasRoutineEnabled = todo.isRoutineEnabled(); // 수정 전 상태 저장

        // 루틴 -> 일반 막기 ( 걍 삭제 하시길.)
        if (wasRoutineEnabled && !request.isRoutineEnabled()) {
            throw new GeneralException(ErrorCode.TODO_ROUTINE_TO_NORMAL_NOT_SUPPORTED);
        }

        // 카테고리  검증
        Category category = getCategoryOrThrow(user.getId(), request.getCategoryName());

        // 루틴 요청 값 파싱/검증
        boolean routineEnabled = request.isRoutineEnabled();

        RoutineInfo routineInfo = validateRoutineOrThrow(
                routineEnabled,
                request.getStartDate(),
                request.getEndDate(),
                request.getWeek()
        );
        LocalDate startDate = routineInfo.startDate();
        LocalDate endDate = routineInfo.endDate();

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
        List<TodoWeek> todoWeeks = List.of();
        if (routineEnabled) {
            todoWeeks = request.getWeek().stream()
                    .map(week -> TodoWeek.of(todo, week))
                    .toList();
            todoWeekRepository.saveAll(todoWeeks);
        }

        LocalDate today = LocalDate.now();
        // TodoDate 동기화 단계
        // 루틴이었거나 루틴이 될 거면, 미래 TodoDate 정리
        if (wasRoutineEnabled || routineEnabled) {
            todoDateRepository.deleteAllByTodo_IdAndDateGreaterThanEqual(todoId, today);
        }
        // 루틴이면 수정과 함께 루틴 생성
        if (routineEnabled) {
            today = LocalDate.now();
            routineTodoDateGenerator.generate(todo, startDate, endDate, today);
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
        User user = getUserOrThrow(userId);
        Category category = getCategoryOrThrow(user.getId(), request.getCategoryName());

        boolean routineEnabled = request.isRoutineEnabled();

        if (!routineEnabled && date == null) {
            throw new GeneralException(ErrorCode.TODO_DATE_REQUIRED);
        }

        RoutineInfo routineInfo = validateRoutineOrThrow(
                routineEnabled,
                request.getStartDate(),
                request.getEndDate(),
                request.getWeek()
        );
        LocalDate startDate = routineInfo.startDate();
        LocalDate endDate = routineInfo.endDate();

        // 투두 저장
        Todo todo = Todo.create(
                request.getDescription(),
                category,
                routineEnabled,
                startDate,
                endDate
        );
        Todo savedTodo = todoRepository.save(todo);

        // 일반 투두 → TodoDate 1개 생성
        if (!routineEnabled) {
            TodoDate todoDate = TodoDate.create(savedTodo, date);
            todoDateRepository.save(todoDate);
            return TodoDetailResponse.from(savedTodo, List.of());
        }

        // 루틴 투두 → TodoWeek 저장
        List<TodoWeek> todoWeeks = request.getWeek().stream()
                .map(week -> TodoWeek.of(savedTodo, week))
                .toList();
        todoWeekRepository.saveAll(todoWeeks);

        // 루린 투두 -> 생성 시 todoDate 미리 생성
        routineTodoDateGenerator.generate(savedTodo, startDate, endDate, startDate);

        return TodoDetailResponse.from(savedTodo, todoWeeks);
    }

    /** 투두 삭제 **/
    @Transactional
    public void deleteTodo(Long userId, Long todoId, LocalDate date){
        User user = getUserOrThrow(userId);
        Todo todo = getTodoOrThrow(todoId);
        assertOwner(user, todo);

        // '그 날짜'의 투두 date 삭제
        TodoDate todoDate = todoDateRepository.findByTodo_IdAndDate(todoId, date)
                .orElseThrow(() -> new GeneralException(ErrorCode.TODO_DATE_NOT_FOUND));
        todoDateRepository.delete(todoDate);

        // 루틴 없는 투두 && todoDate 에 0개 -> 투두테이블에서 하드딜리트
        if (!todo.isRoutineEnabled() && !todoDateRepository.existsByTodo_Id(todoId)) {
                 todoRepository.delete(todo);
        }
    }

    /** 완료 처리 **/
    @Transactional
    public TodoListResponse todoComplete(Long userId, Long todoId, LocalDate date, boolean completed) {
        User user = getUserOrThrow(userId);
        Todo todo = getTodoOrThrow(todoId);
        assertOwner(user, todo);

        // TodoDate 존재 검증
        TodoDate todoDate = todoDateRepository.findByTodo_IdAndDate(todoId, date)
                .orElseThrow(() -> new GeneralException(ErrorCode.TODO_DATE_NOT_FOUND));

        if (todoDate.isCompleted() != completed) {
            todoDate.setCompleted(completed);
        }
        return TodoListResponse.from(todo, todoDate.isCompleted());
    }

}

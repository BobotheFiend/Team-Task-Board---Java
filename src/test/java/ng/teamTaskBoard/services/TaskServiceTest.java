package ng.teamTaskBoard.services;

import ng.teamTaskBoard.data.models.Member;
import ng.teamTaskBoard.data.models.Task;
import ng.teamTaskBoard.data.models.Team;
import ng.teamTaskBoard.data.models.Todo;
import ng.teamTaskBoard.data.models.enums.Priority;
import ng.teamTaskBoard.data.repositories.MemberRepository;
import ng.teamTaskBoard.data.repositories.TaskRepository;
import ng.teamTaskBoard.data.repositories.TeamRepository;
import ng.teamTaskBoard.data.repositories.TodoRepository;
import ng.teamTaskBoard.dtos.requests.CreateTaskRequest;
import ng.teamTaskBoard.dtos.requests.CreateTodoRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private TaskService taskService;

    private Member leader;
    private Member normalMember;
    private Team team;

    @BeforeEach
    void setUp() {

        leader = new Member();
        leader.setId(1);
        leader.setName("CJ");
        leader.setEmail("cj@example.com");
        leader.setActive(true);

        normalMember = new Member();
        normalMember.setId(2);
        normalMember.setName("John");
        normalMember.setEmail("john@example.com");
        normalMember.setActive(true);

        team = new Team();
        team.setId(10);
        team.setName("Development Team");

        team.setLeaderId(leader.getId());
        team.setMemberId(normalMember.getId());
    }

    @Test
    void shouldThrowExceptionWhenCurrentUserDoesNotExist() {

        CreateTaskRequest request = new CreateTaskRequest();
        request.setTaskTitle("Build Login Page");
        request.setTeamId("10");

        when(memberRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> taskService.createTask(request, 99L)
        );

        assertEquals("User not found", exception.getMessage());

        verify(teamRepository, never()).findById(any());
        verify(taskRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotLoggedIn() {

        Member inactiveMember = new Member();
        inactiveMember.setId(3);
        inactiveMember.setName("Inactive User");
        inactiveMember.setActive(false);

        CreateTaskRequest request = new CreateTaskRequest();
        request.setTaskTitle("Build Login Page");
        request.setTeamId("10");

        when(memberRepository.findById(3L))
                .thenReturn(Optional.of(inactiveMember));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> taskService.createTask(request, 3L)
        );

        assertEquals("User must be logged in", exception.getMessage());

        verify(teamRepository, never()).findById(any());
        verify(taskRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenTeamDoesNotExist() {

        CreateTaskRequest request = new CreateTaskRequest();
        request.setTaskTitle("Build Login Page");
        request.setTeamId("10");

        when(memberRepository.findById(1L))
                .thenReturn(Optional.of(leader));

        when(teamRepository.findById(10L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> taskService.createTask(request, 1L)
        );

        assertEquals("Team not found", exception.getMessage());

        verify(taskRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotTeamLeader() {

        CreateTaskRequest request = new CreateTaskRequest();
        request.setTaskTitle("Build Login Page");
        request.setTeamId("10");

        when(memberRepository.findById(2L))
                .thenReturn(Optional.of(normalMember));

        when(teamRepository.findById(10L))
                .thenReturn(Optional.of(team));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> taskService.createTask(request, 2L)
        );

        assertEquals(
                "Only the team leader can create a task",
                exception.getMessage()
        );

        verify(taskRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenTaskTitleAlreadyExists() {

        CreateTaskRequest request = new CreateTaskRequest();
        request.setTaskTitle("Build Login Page");
        request.setTeamId("10");

        when(memberRepository.findById(1L))
                .thenReturn(Optional.of(leader));

        when(teamRepository.findById(10L))
                .thenReturn(Optional.of(team));

        when(taskRepository.existsByTitle("Build Login Page"))
                .thenReturn(true);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> taskService.createTask(request, 1L)
        );

        assertEquals(
                "Task with this title already exists",
                exception.getMessage()
        );

        verify(taskRepository, never()).save(any());
    }

    @Test
    void shouldCreateTaskSuccessfullyWithoutTodos() {

        CreateTaskRequest request = new CreateTaskRequest();
        request.setTaskTitle("Build Login Page");
        request.setTeamId("10");

        LocalDateTime dueDate = LocalDateTime.of(
                2026,
                9,
                20,
                18,
                0
        );

        request.setDueDate(dueDate);
        request.setTodos(null);

        Task savedTask = new Task();
        savedTask.setId(100);
        savedTask.setTitle("Build Login Page");
        savedTask.setTeamId(10);
        savedTask.setDueDate(dueDate);

        when(memberRepository.findById(1L))
                .thenReturn(Optional.of(leader));

        when(teamRepository.findById(10L))
                .thenReturn(Optional.of(team));

        when(taskRepository.existsByTitle("Build Login Page"))
                .thenReturn(false);

        when(taskRepository.save(any(Task.class)))
                .thenReturn(savedTask);

        Task result = taskService.createTask(request, 1L);

        assertNotNull(result);
        assertEquals(100, result.getId());
        assertEquals("Build Login Page", result.getTitle());
        assertEquals(10, result.getTeamId());
        assertEquals(dueDate, result.getDueDate());

        verify(taskRepository, times(1)).save(any(Task.class));

        verify(todoRepository, never()).save(any(Todo.class));
    }

    @Test
    void shouldCreateTaskWithTodo() {

        CreateTaskRequest request = new CreateTaskRequest();
        request.setTaskTitle("Build Dashboard");
        request.setTeamId("10");

        CreateTodoRequest todoRequest = new CreateTodoRequest();
        todoRequest.setTitle("Create Dashboard HTML");
        todoRequest.setMemberId(2);
        todoRequest.setPriority(Priority.HIGH);

        request.setTodos(List.of(todoRequest));

        Task savedTask = new Task();
        savedTask.setId(100);
        savedTask.setTitle("Build Dashboard");
        savedTask.setTeamId(10);

        when(memberRepository.findById(1L))
                .thenReturn(Optional.of(leader));

        when(teamRepository.findById(10L))
                .thenReturn(Optional.of(team));

        when(taskRepository.existsByTitle("Build Dashboard"))
                .thenReturn(false);

        when(taskRepository.save(any(Task.class)))
                .thenReturn(savedTask);

        when(memberRepository.findById(2L))
                .thenReturn(Optional.of(normalMember));

        taskService.createTask(request, 1L);

        ArgumentCaptor<Todo> todoCaptor =
                ArgumentCaptor.forClass(Todo.class);

        verify(todoRepository).save(todoCaptor.capture());

        Todo savedTodo = todoCaptor.getValue();

        assertEquals("Create Dashboard HTML", savedTodo.getTitle());
        assertEquals(2, savedTodo.getMemberId());
        assertEquals(100, savedTodo.getTaskId());
        assertEquals(Priority.HIGH, savedTodo.getPriority());
    }

    @Test
    void shouldThrowExceptionWhenTodoMemberDoesNotExist() {

        CreateTaskRequest request = new CreateTaskRequest();
        request.setTaskTitle("Build Dashboard");
        request.setTeamId("10");

        CreateTodoRequest todoRequest = new CreateTodoRequest();
        todoRequest.setTitle("Create Dashboard HTML");
        todoRequest.setMemberId(99);

        request.setTodos(List.of(todoRequest));

        Task savedTask = new Task();
        savedTask.setId(100);

        when(memberRepository.findById(1L))
                .thenReturn(Optional.of(leader));

        when(teamRepository.findById(10L))
                .thenReturn(Optional.of(team));

        when(taskRepository.existsByTitle("Build Dashboard"))
                .thenReturn(false);

        when(taskRepository.save(any(Task.class)))
                .thenReturn(savedTask);

        when(memberRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> taskService.createTask(request, 1L)
        );

        assertEquals(
                "Member 99 not found",
                exception.getMessage()
        );

        verify(todoRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenTodoMemberIsNotPartOfTeam() {

        CreateTaskRequest request = new CreateTaskRequest();
        request.setTaskTitle("Build Dashboard");
        request.setTeamId("10");

        CreateTodoRequest todoRequest = new CreateTodoRequest();
        todoRequest.setTitle("Create Dashboard HTML");
        todoRequest.setMemberId(99);

        request.setTodos(List.of(todoRequest));

        Member outsideMember = new Member();
        outsideMember.setId(99);
        outsideMember.setName("Outside Member");

        Task savedTask = new Task();
        savedTask.setId(100);

        when(memberRepository.findById(1L))
                .thenReturn(Optional.of(leader));

        when(teamRepository.findById(10L))
                .thenReturn(Optional.of(team));

        when(taskRepository.existsByTitle("Build Dashboard"))
                .thenReturn(false);

        when(taskRepository.save(any(Task.class)))
                .thenReturn(savedTask);

        when(memberRepository.findById(99L))
                .thenReturn(Optional.of(outsideMember));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> taskService.createTask(request, 1L)
        );

        assertEquals(
                "Member 99 is not a member of this team",
                exception.getMessage()
        );

        verify(todoRepository, never()).save(any());
    }

    @Test
    void shouldCombineTodoDateAndTimeCorrectly() {

        CreateTaskRequest request = new CreateTaskRequest();
        request.setTaskTitle("Build API");
        request.setTeamId("10");

        CreateTodoRequest todoRequest = new CreateTodoRequest();
        todoRequest.setTitle("Create Login Endpoint");
        todoRequest.setMemberId(2);
        todoRequest.setDueDate(LocalDate.of(2026, 9, 20));
        todoRequest.setDueTime(LocalTime.of(15, 30));

        request.setTodos(List.of(todoRequest));

        Task savedTask = new Task();
        savedTask.setId(200);

        when(memberRepository.findById(1L))
                .thenReturn(Optional.of(leader));

        when(teamRepository.findById(10L))
                .thenReturn(Optional.of(team));

        when(taskRepository.existsByTitle("Build API"))
                .thenReturn(false);

        when(taskRepository.save(any(Task.class)))
                .thenReturn(savedTask);

        when(memberRepository.findById(2L))
                .thenReturn(Optional.of(normalMember));

        taskService.createTask(request, 1L);

        ArgumentCaptor<Todo> todoCaptor =
                ArgumentCaptor.forClass(Todo.class);

        verify(todoRepository).save(todoCaptor.capture());

        Todo savedTodo = todoCaptor.getValue();

        assertEquals(
                LocalDateTime.of(2026, 9, 20, 15, 30),
                savedTodo.getDueDate()
        );
    }

    @Test
    void shouldLeaveTodoDueDateNullWhenDateAndTimeAreNotProvided() {

        CreateTaskRequest request = new CreateTaskRequest();
        request.setTaskTitle("Build API");
        request.setTeamId("10");

        CreateTodoRequest todoRequest = new CreateTodoRequest();
        todoRequest.setTitle("Create Login Endpoint");
        todoRequest.setMemberId(2);

        request.setTodos(List.of(todoRequest));

        Task savedTask = new Task();
        savedTask.setId(200);

        when(memberRepository.findById(1L))
                .thenReturn(Optional.of(leader));

        when(teamRepository.findById(10L))
                .thenReturn(Optional.of(team));

        when(taskRepository.existsByTitle("Build API"))
                .thenReturn(false);

        when(taskRepository.save(any(Task.class)))
                .thenReturn(savedTask);

        when(memberRepository.findById(2L))
                .thenReturn(Optional.of(normalMember));

        taskService.createTask(request, 1L);

        ArgumentCaptor<Todo> todoCaptor =
                ArgumentCaptor.forClass(Todo.class);

        verify(todoRepository).save(todoCaptor.capture());

        Todo savedTodo = todoCaptor.getValue();

        assertNull(savedTodo.getDueDate());
    }

    @Test
    void shouldCreateMultipleTodos() {

        CreateTaskRequest request = new CreateTaskRequest();
        request.setTaskTitle("Build Full Application");
        request.setTeamId("10");

        CreateTodoRequest firstTodo = new CreateTodoRequest();
        firstTodo.setTitle("Build Frontend");
        firstTodo.setMemberId(2);

        CreateTodoRequest secondTodo = new CreateTodoRequest();
        secondTodo.setTitle("Build Backend");
        secondTodo.setMemberId(2);

        request.setTodos(List.of(firstTodo, secondTodo));

        Task savedTask = new Task();
        savedTask.setId(300);

        when(memberRepository.findById(1L))
                .thenReturn(Optional.of(leader));

        when(teamRepository.findById(10L))
                .thenReturn(Optional.of(team));

        when(taskRepository.existsByTitle("Build Full Application"))
                .thenReturn(false);

        when(taskRepository.save(any(Task.class)))
                .thenReturn(savedTask);

        when(memberRepository.findById(2L))
                .thenReturn(Optional.of(normalMember));

        taskService.createTask(request, 1L);

        verify(todoRepository, times(2)).save(any(Todo.class));
    }
}
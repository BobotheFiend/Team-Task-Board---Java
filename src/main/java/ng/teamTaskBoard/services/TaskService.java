package ng.teamTaskBoard.services;

import ng.teamTaskBoard.data.models.Member;
import ng.teamTaskBoard.data.models.Task;
import ng.teamTaskBoard.data.models.Team;
import ng.teamTaskBoard.data.models.Todo;
import ng.teamTaskBoard.data.repositories.MemberRepository;
import ng.teamTaskBoard.data.repositories.TaskRepository;
import ng.teamTaskBoard.data.repositories.TeamRepository;
import ng.teamTaskBoard.data.repositories.TodoRepository;
import ng.teamTaskBoard.dtos.requests.CreateTaskRequest;
import ng.teamTaskBoard.dtos.requests.CreateTodoRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TeamRepository teamRepository;
    private final TodoRepository todoRepository;
    private final MemberRepository memberRepository;

    public TaskService(
            TaskRepository taskRepository,
            TeamRepository teamRepository,
            TodoRepository todoRepository,
            MemberRepository memberRepository
    ) {
        this.taskRepository = taskRepository;
        this.teamRepository = teamRepository;
        this.todoRepository = todoRepository;
        this.memberRepository = memberRepository;
    }

    public Task createTask(CreateTaskRequest request, int currentUserId) {

        // 1. Check if current user exists
        Member currentUser = memberRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Check if user is logged in
        if (!currentUser.isActive()) {
            throw new RuntimeException("User must be logged in");
        }

        // 3. Convert team ID from String to int
        int teamId = Integer.parseInt(request.getTeamId());

        // 4. Check if team exists
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        // 5. Check if current user is the team leader
        if (team.getLeaderId().getId() != currentUserId) {
            throw new RuntimeException("Only the team leader can create a task");
        }

        // 6. Check for duplicate task title
        if (taskRepository.existsByTitle(request.getTaskTitle())) {
            throw new RuntimeException("Task with this title already exists");
        }

        // 7. Create the task
        Task task = new Task();

        task.setTitle(request.getTaskTitle());
        task.setTeamId(teamId);
        task.setDueDate(request.getDueDate());
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());

        // 8. Save the task
        Task savedTask = taskRepository.save(task);

        // 9. Create todos if they were provided
        if (request.getTodos() != null) {

            for (CreateTodoRequest todoRequest : request.getTodos()) {

                // Check that the member exists
                Member member = memberRepository.findById(todoRequest.getMemberId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Member " + todoRequest.getMemberId() + " not found"
                                )
                        );

                // Check that the member belongs to the team
                if (team.getMemberId() != member.getId()) {
                    throw new RuntimeException(
                            "Member " + member.getId() + " is not a member of this team"
                    );
                }

                // Create Todo
                Todo todo = new Todo();

                todo.setTitle(todoRequest.getTitle());
                todo.setMemberId(member.getId());
                todo.setTaskId(savedTask.getId());
                todo.setPriority(todoRequest.getPriority());

                // Combine LocalDate and LocalTime into LocalDateTime
                if (todoRequest.getDueDate() != null &&
                        todoRequest.getDueTime() != null) {

                    todo.setDueDate(
                            LocalDateTime.of(
                                    todoRequest.getDueDate(),
                                    todoRequest.getDueTime()
                            )
                    );
                }

                todo.setCreatedAt(LocalDateTime.now());
                todo.setUpdatedAt(LocalDateTime.now());

                todoRepository.save(todo);
            }
        }

        return savedTask;
    }
}
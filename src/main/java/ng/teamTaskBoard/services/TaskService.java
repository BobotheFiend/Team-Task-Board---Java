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

    public Task createTask(CreateTaskRequest request, Long currentUserId) {

        Member currentUser = memberRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!currentUser.isActive()) {
            throw new RuntimeException("User must be logged in");
        }

        long teamId = Long.parseLong(request.getTeamId());

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        if (team.getLeaderId() != currentUserId) {
            throw new RuntimeException("Only the team leader can create a task");
        }

        if (taskRepository.existsByTitle(request.getTaskTitle())) {
            throw new RuntimeException("Task with this title already exists");
        }

        Task task = new Task();

        task.setTitle(request.getTaskTitle());
        task.setTeamId(teamId);
        task.setDueDate(request.getDueDate());
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());

        Task savedTask = taskRepository.save(task);

        if (request.getTodos() != null) {

            for (CreateTodoRequest todoRequest : request.getTodos()) {

                Member member = memberRepository.findById(todoRequest.getMemberId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Member " + todoRequest.getMemberId() + " not found"
                                )
                        );

                if (team.getMemberId() != member.getId()) {
                    throw new RuntimeException(
                            "Member " + member.getId() + " is not a member of this team"
                    );
                }

                Todo todo = new Todo();

                todo.setTitle(todoRequest.getTitle());
                todo.setMemberId(member.getId());
                todo.setTaskId(savedTask.getId());
                todo.setPriority(todoRequest.getPriority());

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
package ng.teamTaskBoard.controllers;

import ng.teamTaskBoard.data.models.Task;
import ng.teamTaskBoard.dtos.requests.CreateTaskRequest;
import ng.teamTaskBoard.services.TaskService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public Task createTask(
            @RequestBody CreateTaskRequest request,
            @RequestParam int currentUserId
    ) {
        return taskService.createTask(request, currentUserId);
    }
}
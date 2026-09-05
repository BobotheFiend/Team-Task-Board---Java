package ng.teamTaskBoard.dtos.requests;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateTaskRequest {

    private String taskTitle;
    private  String teamId;
    private LocalDateTime dueDate;
    private List<CreateTodoRequest> todos;
}

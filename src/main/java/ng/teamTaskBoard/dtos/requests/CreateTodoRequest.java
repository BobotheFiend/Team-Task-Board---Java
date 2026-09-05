package ng.teamTaskBoard.dtos.requests;

import lombok.Data;
import ng.teamTaskBoard.data.models.enums.Priority;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CreateTodoRequest {

    private int taskId;
    private int memberId;
    private String title;
    private String memberEmail;
    private Priority priority;
    private LocalDate dueDate;
    private LocalTime dueTime;

}

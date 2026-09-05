package ng.teamTaskBoard.dtos.requests;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class UpdateTaskRequest {

    private String taskName;
    private LocalDate dueDate;
    private LocalTime dueTime;
    private String teamName;

}

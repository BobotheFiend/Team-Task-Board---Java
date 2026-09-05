package ng.teamTaskBoard.dtos.requests;

import lombok.Data;
import ng.teamTaskBoard.data.models.enums.Priority;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class UpdateTodorRequest {

    private String todoTitle;
    private String memberId;
    private String teamName;
    private Priority priority;
    private LocalDate dueDate;
    private LocalTime dueTime;

}

package ng.teamTaskBoard.data.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Data;
import ng.teamTaskBoard.data.models.enums.Priority;
import ng.teamTaskBoard.data.models.enums.Status;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "todos")
public class Todo {

    @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long  id;
    private  String title;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int position;
    private  long memberId;
    private long taskId;
    private  Priority priority;
    private Status status =  Status.IN_PROGRESS;
    private LocalDateTime dueDate;

}

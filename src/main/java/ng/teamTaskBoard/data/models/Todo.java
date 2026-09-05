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
    int  id;
    String title;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    int position;
    int memberId;
    int taskId;
    Priority priority;
    Status status =  Status.IN_PROGRESS;
    LocalDateTime dueDate;

}

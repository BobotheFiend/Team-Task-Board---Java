package ng.teamTaskBoard.data.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Data;
import ng.teamTaskBoard.data.models.enums.Role;
import org.springframework.data.annotation.Id;

@Data
@Entity
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String email;
    String name;
    String password;
    Role role;
    boolean isActive;
}

package ng.teamTaskBoard.dtos.requests;

import lombok.Data;
import ng.teamTaskBoard.data.models.enums.Role;

@Data
public class RegisterMemberRequest {

    private String email;
    private String name;
    private String password;
    private Role role;
}
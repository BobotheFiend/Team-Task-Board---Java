package ng.teamTaskBoard.dtos.responses;

import lombok.Data;
import ng.teamTaskBoard.data.models.enums.Role;

@Data
public class RegisterMemberResponse {

    private Long id;
    private String name;
    private String email;
    private Role role;

}
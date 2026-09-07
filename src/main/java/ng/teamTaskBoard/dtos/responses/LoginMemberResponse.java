package ng.teamTaskBoard.dtos.responses;

import lombok.Data;
import ng.teamTaskBoard.data.models.enums.Role;

@Data
public class LoginMemberResponse {

    private long id;
    private String name;
    private String email;
    private Role role;

}
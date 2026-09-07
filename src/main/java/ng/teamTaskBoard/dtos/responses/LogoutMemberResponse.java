package ng.teamTaskBoard.dtos.responses;

import lombok.Data;

@Data
public class LogoutMemberResponse {

    private String email;
    private String message;

}
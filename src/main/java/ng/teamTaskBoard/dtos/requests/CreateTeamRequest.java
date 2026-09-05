package ng.teamTaskBoard.dtos.requests;

import lombok.Data;

import java.util.List;

@Data
public class CreateTeamRequest {

    private String teamName;
    private List<String> teamMemberEmail;
    private String teamLeaderEmail;

}

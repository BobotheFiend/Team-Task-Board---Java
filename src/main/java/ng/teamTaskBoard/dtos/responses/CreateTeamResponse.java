package ng.teamTaskBoard.dtos.responses;

import lombok.Data;
import ng.teamTaskBoard.data.models.Member;

import java.util.List;

@Data
public class CreateTeamResponse {

    String teamName;
    List<String> teamMembersEmail;
    Member lead;
}

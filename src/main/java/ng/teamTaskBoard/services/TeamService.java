package ng.teamTaskBoard.services;

import ng.teamTaskBoard.dtos.requests.CreateTeamRequest;
import ng.teamTaskBoard.dtos.responses.CreateTeamResponse;

public interface TeamService {

    CreateTeamResponse  createTeam(CreateTeamRequest createTeamRequest);
}

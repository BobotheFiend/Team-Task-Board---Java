package ng.teamTaskBoard.services;

import lombok.AllArgsConstructor;
import ng.teamTaskBoard.data.repositories.MemberRepository;
import ng.teamTaskBoard.data.repositories.TeamRepository;
import ng.teamTaskBoard.dtos.requests.CreateTeamRequest;
import ng.teamTaskBoard.dtos.responses.CreateTeamResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class TeamServiceImpl implements TeamService {


    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;

    @Override
    public CreateTeamResponse createTeam(CreateTeamRequest createTeamRequest) {

        return null;
    }

}

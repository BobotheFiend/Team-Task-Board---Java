package ng.teamTaskBoard.data.repositories;

import ng.teamTaskBoard.data.models.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team,Integer> {
}

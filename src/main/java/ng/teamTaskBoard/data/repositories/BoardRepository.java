package ng.teamTaskBoard.data.repositories;

import ng.teamTaskBoard.data.models.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board,Integer> {
}

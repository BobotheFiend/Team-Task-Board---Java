package ng.teamTaskBoard.data.repositories;

import ng.teamTaskBoard.data.models.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member,Integer> {
}


package utcapitole.miage.projetDevG3.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import utcapitole.miage.projetDevG3.model.Reaction;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {

}
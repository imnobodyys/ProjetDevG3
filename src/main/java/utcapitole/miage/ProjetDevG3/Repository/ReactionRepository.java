
package utcapitole.miage.projetDevG3.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import utcapitole.miage.projetDevG3.model.Post;
import utcapitole.miage.projetDevG3.model.Reaction;
import utcapitole.miage.projetDevG3.model.Utilisateur;


@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {

    List<Reaction> findByPost(Post post);

    List<Reaction> findByExpedient(Utilisateur expedient);
}
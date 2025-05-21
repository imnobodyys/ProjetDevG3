package utcapitole.miage.projetdevg3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import utcapitole.miage.projetdevg3.model.ConversationGrp;


@Repository
public interface ConversationGrpRepository extends JpaRepository<ConversationGrp, Long> {
    // Méthode pour trouver une conversation de groupe par son ID

}

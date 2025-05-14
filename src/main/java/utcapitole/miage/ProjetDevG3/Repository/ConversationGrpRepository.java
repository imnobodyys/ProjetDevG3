package utcapitole.miage.projetDevG3.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import utcapitole.miage.projetDevG3.model.ConversationGrp;

@Repository
public interface ConversationGrpRepository extends JpaRepository <ConversationGrp, Long> {
    // Méthode pour trouver une conversation de groupe par son ID
    ConversationGrp findById(long id);

}

package utcapitole.miage.projetDevG3.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import utcapitole.miage.projetDevG3.model.Conversation;
import utcapitole.miage.projetDevG3.model.ConversationPri;
import utcapitole.miage.projetDevG3.model.Utilisateur;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    // Méthode pour trouver une conversation par son ID

}

package utcapitole.miage.projetDevG3.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import utcapitole.miage.projetDevG3.model.Conversation;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    // Méthode pour trouver une conversation par son ID
    Conversation findById(long id);

}

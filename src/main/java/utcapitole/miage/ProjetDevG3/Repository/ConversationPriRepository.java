package utcapitole.miage.projetDevG3.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import utcapitole.miage.projetDevG3.model.ConversationPri;

@Repository
public interface ConversationPriRepository extends JpaRepository<ConversationPri, Long> {
    // Méthode pour trouver une conversation privée par son ID
    ConversationPri findById(long id);

    // Méthode pour trouver une conversation privée par son nom
    ConversationPri findByNom(String nom);

}

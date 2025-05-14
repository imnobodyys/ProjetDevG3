package utcapitole.miage.projetDevG3.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import utcapitole.miage.projetDevG3.model.Conversation;
import utcapitole.miage.projetDevG3.model.Utilisateur;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    // Méthode pour trouver une conversation par son ID
    Conversation findById(long id);
    // Méthode pour trouver toutes les conversations d'un utilisateur
    List<Conversation> findByUtilisateur(Utilisateur utilisateur);

}

package utcapitole.miage.projetDevG3.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import utcapitole.miage.projetDevG3.model.Conversation;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    // Méthode pour trouver une conversation par son ID
    Conversation findById(long id);
<<<<<<< HEAD
    // Méthode pour trouver toutes les conversations d'un utilisateur
    List<Conversation> findByUtilisateur(Utilisateur utilisateur);
    
=======
>>>>>>> 21e01feecfda03068423082881787793ecd475c7

}

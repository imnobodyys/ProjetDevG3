package utcapitole.miage.projetDevG3.Repository;

import java.util.List;

import utcapitole.miage.projetDevG3.model.Commentaire;
import utcapitole.miage.projetDevG3.model.Conversation;
import utcapitole.miage.projetDevG3.model.Post;
import utcapitole.miage.projetDevG3.model.Utilisateur;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    // Tous les commentaires liés à un post donné
    List<Commentaire> findByPost(Post post);

    // Tous les commentaires envoyés par un utilisateur
    List<Commentaire> findByExpediteur(Utilisateur expediteur);
    
}

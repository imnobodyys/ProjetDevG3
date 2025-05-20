package utcapitole.miage.projetdevg3.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import utcapitole.miage.projetdevg3.model.ConversationPri;
import utcapitole.miage.projetdevg3.model.Utilisateur;

@Repository
public interface ConversationPriRepository extends JpaRepository<ConversationPri, Long> {

    /**
     * pour savoir conversation entre deux personne
     * 
     * @param a1
     * @param b1
     * @param a2
     * @param b2
     * @return conversationpri
     */
    Optional<ConversationPri> findByExpediteurCPAndDestinataireCPOrExpediteurCPAndDestinataireCP(
            Utilisateur a1, Utilisateur b1,
            Utilisateur a2, Utilisateur b2);

    /**
     * 
     * @param a
     * @param b
     * @returnlist conversationpri
     */
    List<ConversationPri> findByExpediteurCPOrDestinataireCP(Utilisateur a, Utilisateur b);

}

package utcapitole.miage.projetdevg3.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    @Query("""
                SELECT c FROM ConversationPri c
                WHERE (c.expediteurCP = :u1 AND c.destinataireCP = :u2)
                   OR (c.expediteurCP = :u2 AND c.destinataireCP = :u1)
            """)
    Optional<ConversationPri> findConversationBetween(@Param("u1") Utilisateur u1, @Param("u2") Utilisateur u2);

    /**
     * 
     * @param a
     * @param b
     * @returnlist conversationpri
     */
    List<ConversationPri> findByExpediteurCPOrDestinataireCP(Utilisateur a, Utilisateur b);

}

package utcapitole.miage.projetdevg3.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import utcapitole.miage.projetdevg3.model.Conversation;
import utcapitole.miage.projetdevg3.model.Message;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

/**
 * MessageRepository est une interface qui étend JpaRepository pour gérer
 * les opérations CRUD sur les entités Message.
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Récupère tous les messages envoyés par un utilisateur spécifique.
     *
     * @param expedi L'utilisateur expéditeur du message.
     * @return Une liste de messages envoyés par l'utilisateur donné.
     */
    List<Message> findByExpedi(Utilisateur expedi);

    /**
     * Récupère tous les messages associés à une conversation donnée.
     *
     * @param conversation La conversation dont on veut récupérer les messages.
     * @return Une liste de messages appartenant à la conversation spécifiée.
     */
    List<Message> findByConversation(Conversation conversation);

    /**
     * Récupère tous les messages associés à une conversation id
     * 
     * @param conversationId
     * @return
     */
    List<Message> findByConversationId(Long conversationId);

    /**
     * Récupère tous les messages d'une conversation, triés par date d'envoi
     * croissante.
     *
     * @param conversation La conversation dont on veut récupérer les messages.
     * @return Une liste de messages classés par ordre chronologique ascendant.
     */
    List<Message> findByConversationOrderByDtEnvoiAsc(Conversation conversation);

    /**
     * pour touver message prive de utlisateur
     * 
     * @param utilisateur
     * @param pageable
     * @return
     */
    @Query("""
            SELECT m FROM Message m
            JOIN ConversationPri c ON c = m.conversation
            WHERE TYPE(m.conversation) = ConversationPri
            AND (c.expediteurCP = :utilisateur OR c.destinataireCP = :utilisateur)
            ORDER BY m.dtEnvoi DESC
            """)
    List<Message> findPrivateMessagesForUser(Utilisateur utilisateur, Pageable pageable);

    /**
     * pour touver message group de utlisateur
     * 
     * @param utilisateur
     * @param pageable
     * @return
     */
    @Query("""
            SELECT m FROM Message m
            JOIN ConversationGrp cg ON cg = m.conversation
            JOIN Groupe g ON g = cg.groupeCon
            LEFT JOIN MembreGroupe mg ON mg.groupe = g
            WHERE g.createur = :utilisateur OR mg.membre = :utilisateur
            ORDER BY m.dtEnvoi DESC
            """)
    List<Message> findGroupMessagesForUser(Utilisateur utilisateur, Pageable pageable);
}
package utcapitole.miage.projetdevg3.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import utcapitole.miage.projetdevg3.model.Conversation;
import utcapitole.miage.projetdevg3.model.Message;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import org.springframework.data.domain.Pageable;

/**
 * Repository pour la gestion des opérations CRUD sur les entités Message.
 * Fournit des méthodes spécifiques pour interagir avec les messages dans la
 * base de données.
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

        /**
         * Récupère tous les messages envoyés par un utilisateur spécifique.
         *
         * @param expedi L'utilisateur expéditeur des messages à rechercher
         * @return Liste des messages envoyés par l'utilisateur, peut être vide si aucun
         *         message trouvé
         */
        List<Message> findByExpedi(Utilisateur expedi);

        /**
         * Récupère tous les messages associés à une conversation spécifique.
         *
         * @param conversation La conversation pour laquelle rechercher les messages
         * @return Liste des messages de la conversation, peut être vide si aucun
         *         message trouvé
         */
        List<Message> findByConversation(Conversation conversation);

        /**
         * Récupère tous les messages associés à une conversation identifiée par son ID.
         *
         * @param conversationId L'identifiant de la conversation pour laquelle
         *                       rechercher les messages
         * @return Liste des messages de la conversation, peut être vide si aucun
         *         message trouvé
         */
        List<Message> findByConversationId(Long conversationId);

        /**
         * Récupère les messages d'une conversation triés par date d'envoi ascendante.
         *
         * @param conversation La conversation pour laquelle rechercher les messages
         * @return Liste des messages triés par date d'envoi croissante
         */
        List<Message> findByConversationOrderByDtEnvoiAsc(Conversation conversation);

        /**
         * Récupère les messages d'une conversation triés par date d'envoi descendante.
         * 
         * @param conversation La conversation pour laquelle rechercher les messages
         * @param pageable     Paramètres de pagination
         * @return Liste des messages triés par date d'envoi décroissante
         */
        List<Message> findByConversationOrderByDtEnvoiDesc(Conversation conversation, Pageable pageable);

        /**
         * Récupère les messages privés récents d'un utilisateur.
         * Les messages sont triés par date d'envoi descendante et peuvent être paginés.
         *
         * @param utilisateur L'utilisateur pour lequel rechercher les messages privés
         * @param pageable    Paramètres de pagination (nombre de résultats, etc.)
         * @return Liste paginée des messages privés triés du plus récent au plus ancien
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
         * Récupère les messages de groupe récents d'un utilisateur.
         * Inclut les messages des groupes où l'utilisateur est créateur ou membre.
         * Les messages sont triés par date d'envoi descendante et peuvent être paginés.
         *
         * @param utilisateur L'utilisateur pour lequel rechercher les messages de
         *                    groupe
         * @param pageable    Paramètres de pagination (nombre de résultats, etc.)
         * @return Liste paginée des messages de groupe triés du plus récent au plus
         *         ancien
         */
        @Query("""
                        SELECT m FROM Message m
                        JOIN ConversationGrp cg ON cg = m.conversation
                        JOIN Groupe g ON g = cg.groupeCon
                        LEFT JOIN MembreGroupe mg ON mg.groupe = g
                        WHERE g.createur = :utilisateur OR mg.membreUtilisateur  = :utilisateur
                        ORDER BY m.dtEnvoi DESC
                        """)
        List<Message> findGroupMessagesForUser(Utilisateur utilisateur, Pageable pageable);
}
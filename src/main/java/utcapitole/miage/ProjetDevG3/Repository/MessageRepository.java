package utcapitole.miage.projetDevG3.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import utcapitole.miage.projetDevG3.model.Conversation;
import utcapitole.miage.projetDevG3.model.Message;
import utcapitole.miage.projetDevG3.model.Utilisateur;

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
     * Récupère tous les messages d'une conversation, triés par date d'envoi
     * croissante.
     *
     * @param conversation La conversation dont on veut récupérer les messages.
     * @return Une liste de messages classés par ordre chronologique ascendant.
     */
    List<Message> findByConversationOrderByDtEnvoiAsc(Conversation conversation);

}
package utcapitole.miage.projetDevG3.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import utcapitole.miage.projetDevG3.Repository.ConversationPriRepository;
import utcapitole.miage.projetDevG3.Repository.ConversationRepository;
import utcapitole.miage.projetDevG3.model.ConversationPri;
import utcapitole.miage.projetDevG3.model.Utilisateur;

/**
 * Service générique pour la gestion des conversations.
 * Centralise les opérations communes à tous les types de conversations.
 */
@Service
@RequiredArgsConstructor
public class ConversationService {

    /**
     * Référentiel pour les conversations.
     * Utilisé pour interagir avec la base de données.
     */
    private final ConversationRepository conversationRepository;
    private final ConversationPriRepository conversationPriRepository;

    /**
     * method pour avoir touts conversation de utlisateur
     * 
     * @param utilisateur
     * @return
     */
    public List<ConversationPri> getConversationsOfUser(Utilisateur utilisateur) {
        return conversationPriRepository.findByExpediteurCPOrDestinataireCP(utilisateur, utilisateur);
    }
}
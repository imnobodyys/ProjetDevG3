package utcapitole.miage.projetdevg3.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import utcapitole.miage.projetdevg3.repository.ConversationGrpRepository;
import utcapitole.miage.projetdevg3.repository.ConversationPriRepository;
import utcapitole.miage.projetdevg3.repository.ConversationRepository;
import utcapitole.miage.projetdevg3.repository.MessageRepository;
import utcapitole.miage.projetdevg3.repository.UtilisateurRepository;
import utcapitole.miage.projetdevg3.model.ConversationGrp;
import utcapitole.miage.projetdevg3.model.ConversationPri;
import utcapitole.miage.projetdevg3.model.Message;
import utcapitole.miage.projetdevg3.model.Utilisateur;

/**
 * Service générique pour la gestion des conversations.
 * Centralise les opérations communes à tous les types de conversations.
 */
/**
 * Service pour la gestion des conversations entre utilisateurs.
 * Fournit des méthodes pour récupérer les conversations avec leurs messages
 * récents.
 */
@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationPriRepository conversationPriRepository;
    private final ConversationGrpRepository conversationGrpRepository;
    private final MessageRepository messageRepository;
    private static final Pageable TOP_FIVE = PageRequest.of(0, 5);

    /**
     * Récupère toutes les conversations privées d'un utilisateur avec leurs 5
     * derniers messages
     * 
     * @param utilisateur l'utilisateur dont on veut les conversations
     * @return liste des conversations privées avec messages récents
     */
    public List<ConversationPri> getConversationsWithRecentMessages(Utilisateur utilisateur) {
        List<ConversationPri> conversations = conversationPriRepository
                .findByExpediteurCPOrDestinataireCP(utilisateur, utilisateur);

        conversations.forEach(conv -> {
            List<Message> recentMessages = messageRepository
                    .findByConversationOrderByDtEnvoiDesc(conv, TOP_FIVE);
            conv.setRecentMessages(recentMessages);
        });

        return conversations;
    }

    /**
     * Récupère toutes les conversations de groupe d'un utilisateur avec leurs 5
     * derniers messages
     * 
     * @param utilisateur l'utilisateur dont on veut les conversations
     * @return liste des conversations de groupe avec messages récents
     */
    public List<ConversationGrp> getGroupConversationsWithRecentMessages(Utilisateur utilisateur) {
        List<ConversationGrp> conversations = conversationGrpRepository
                .findByUtilisateur(utilisateur);

        conversations.forEach(conv -> {
            List<Message> recentMessages = messageRepository
                    .findByConversationOrderByDtEnvoiDesc(conv, TOP_FIVE);
            conv.setRecentMessages(recentMessages);
        });

        return conversations;
    }

    /**
     * Récupère tous les messages d'une conversation spécifique
     * 
     * @param conversationId l'identifiant de la conversation
     * @param utilisateur    l'utilisateur qui fait la requête (pour vérification
     *                       d'accès)
     * @return liste des messages de la conversation
     * @throws IllegalArgumentException si la conversation n'existe pas
     * @throws IllegalStateException    si l'utilisateur n'a pas accès à la
     *                                  conversation
     */
    public List<Message> getMessagesForConversation(Long conversationId, Utilisateur utilisateur) {
        ConversationPri conversation = conversationPriRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation non trouvée"));

        if (!conversation.getExpediteurCP().equals(utilisateur) &&
                !conversation.getDestinataireCP().equals(utilisateur)) {
            throw new IllegalStateException("Vous n'avez pas accès à cette conversation.");
        }

        return messageRepository.findByConversationOrderByDtEnvoiAsc(conversation);
    }

    /**
     * Récupère l'autre utilisateur d'une conversation privée
     * 
     * @param conversationId l'identifiant de la conversation
     * @param utilisateur    l'utilisateur courant
     * @return l'autre participant à la conversation
     * @throws IllegalArgumentException si la conversation n'existe pas
     */
    public Utilisateur getOtherUser(Long conversationId, Utilisateur utilisateur) {
        ConversationPri conversation = conversationPriRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation non trouvée"));

        return conversation.getExpediteurCP().equals(utilisateur)
                ? conversation.getDestinataireCP()
                : conversation.getExpediteurCP();
    }
}
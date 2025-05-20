package utcapitole.miage.projetdevg3.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import utcapitole.miage.projetdevg3.repository.ConversationPriRepository;
import utcapitole.miage.projetdevg3.repository.ConversationRepository;
import utcapitole.miage.projetdevg3.repository.MessageRepository;
import utcapitole.miage.projetdevg3.repository.UtilisateurRepository;

import utcapitole.miage.projetdevg3.model.ConversationPri;
import utcapitole.miage.projetdevg3.model.Message;
import utcapitole.miage.projetdevg3.model.Utilisateur;

/**
 * Service générique pour la gestion des conversations.
 * Centralise les opérations communes à tous les types de conversations.
 */
@Service
public class ConversationService {

    /**
     * Référentiel pour les conversations.
     * Utilisé pour interagir avec la base de données.
     */
    private final ConversationRepository conversationRepository;
    private final ConversationPriRepository conversationPriRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final MessageRepository messageRepository;

    public ConversationService(ConversationPriRepository conversationPriRepository, ConversationRepository conversationRepository, MessageRepository messageRepository, UtilisateurRepository utilisateurRepository) {
        this.conversationPriRepository = conversationPriRepository;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * method pour avoir touts conversation de utlisateur
     * 
     * @param utilisateur
     * @return
     */
    public List<ConversationPri> getConversationsOfUser(Utilisateur utilisateur) {
        return conversationPriRepository.findByExpediteurCPOrDestinataireCP(utilisateur, utilisateur);
    }

    public List<Message> getMessagesForConversation(Long conversationId, Utilisateur utilisateur) {
        ConversationPri conversation = conversationPriRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation non trouvée"));

        if (!conversation.getExpediteurCP().equals(utilisateur) &&
                !conversation.getDestinataireCP().equals(utilisateur)) {
            throw new IllegalStateException("Vous n'avez pas accès à cette conversation.");
        }

        return messageRepository.findByConversationOrderByDtEnvoiAsc(conversation);
    }

    public Utilisateur getOtherUser(Long conversationId, Utilisateur utilisateur) {

        ConversationPri conversation = conversationPriRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation non trouvée"));

        return conversation.getExpediteurCP().equals(utilisateur)
                ? conversation.getDestinataireCP()
                : conversation.getExpediteurCP();
    }
}
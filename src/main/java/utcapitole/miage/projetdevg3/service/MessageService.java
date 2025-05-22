package utcapitole.miage.projetdevg3.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import utcapitole.miage.projetdevg3.model.Conversation;
import utcapitole.miage.projetdevg3.model.ConversationGrp;
import utcapitole.miage.projetdevg3.model.ConversationPri;
import utcapitole.miage.projetdevg3.model.Message;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.repository.ConversationGrpRepository;
import utcapitole.miage.projetdevg3.repository.ConversationPriRepository;
import utcapitole.miage.projetdevg3.repository.MessageRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

/**
 * Service pour la gestion des messages.
 * Contrôle l'envoi, la modification et la suppression des communications
 * utilisateur.
 */
/**
 * Service pour la gestion des messages entre utilisateurs.
 * Gère l'envoi, la récupération et la suppression des messages.
 */
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationPriRepository conversationPriRepository;
    private final ConversationGrpRepository conversationGrpRepository;
    private static final Pageable TOP_FIVE = PageRequest.of(0, 5);

    /**
     * Envoie un message dans une conversation privée
     * 
     * @param conversationId l'identifiant de la conversation
     * @param expediteur     l'utilisateur qui envoie le message
     * @param contenu        le contenu du message
     * @throws IllegalArgumentException si la conversation n'existe pas
     */
    @Transactional
    public void envoyerMessagePrive(Long conversationId, Utilisateur expediteur, String contenu) {
        ConversationPri conversation = conversationPriRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation non trouvée"));

        Message message = new Message();
        message.setExpedi(expediteur);
        message.setConversation(conversation);
        message.setContenu(contenu);
        message.setDtEnvoi(LocalDateTime.now());

        messageRepository.save(message);
    }

    /**
     * Envoie un message dans une conversation de groupe
     * 
     * @param conversationId l'identifiant de la conversation de groupe
     * @param expediteur     l'utilisateur qui envoie le message
     * @param contenu        le contenu du message
     * @throws IllegalArgumentException si la conversation n'existe pas
     */
    @Transactional
    public void envoyerMessageGroupe(Long conversationId, Utilisateur expediteur, String contenu) {
        ConversationGrp conversation = conversationGrpRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation de groupe non trouvée"));

        Message message = new Message();
        message.setExpedi(expediteur);
        message.setConversation(conversation);
        message.setContenu(contenu);
        message.setDtEnvoi(LocalDateTime.now());

        messageRepository.save(message);
    }

    /**
     * Récupère tous les messages d'une conversation
     * 
     * @param conversationId l'identifiant de la conversation
     * @return liste des messages de la conversation
     */
    public List<Message> getMessagesByConversationId(Long conversationId) {
        return messageRepository.findByConversationId(conversationId);
    }

    /**
     * Récupère les 5 derniers messages privés d'un utilisateur
     * 
     * @param utilisateur l'utilisateur dont on veut les messages
     * @return liste des 5 derniers messages privés
     */
    public List<Message> getRecentPriMessages(Utilisateur utilisateur) {
        return messageRepository.findPrivateMessagesForUser(utilisateur, TOP_FIVE);
    }

    /**
     * Récupère les 5 derniers messages de groupe d'un utilisateur
     * 
     * @param utilisateur l'utilisateur dont on veut les messages
     * @return liste des 5 derniers messages de groupe
     */
    public List<Message> getRecentGroupMessages(Utilisateur utilisateur) {
        return messageRepository.findGroupMessagesForUser(utilisateur, TOP_FIVE);
    }

    /**
     * Récupère tous les messages d'un groupe spécifique
     * 
     * @param groupeId l'identifiant du groupe
     * @return liste des messages du groupe
     * @throws IllegalArgumentException si le groupe n'existe pas
     */
    public List<Message> listerMessagesDuGroupe(Long groupeId) {
        ConversationGrp conversationGrp = conversationGrpRepository.findByGroupeCon_Id(groupeId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation de groupe introuvable"));
        return conversationGrp.getMessages();
    }
}
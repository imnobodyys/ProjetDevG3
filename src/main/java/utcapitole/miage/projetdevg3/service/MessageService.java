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
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationPriRepository conversationRepository;
    private final ConversationGrpRepository conversationGrpRepository;

    public MessageService(ConversationPriRepository conversationRepository, MessageRepository messageRepository,
            ConversationGrpRepository conversationGrpRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.conversationGrpRepository = conversationGrpRepository;
    }

    /**
     * Envoie un message privé dans une conversation
     */
    @Transactional
    public void envoyerMessage(Utilisateur expediteur, Utilisateur destinataire, String contenu) {
        // Vérifie s'il y a déjà une conversation
        ConversationPri conversation = conversationRepository
                .findByExpediteurCPAndDestinataireCPOrExpediteurCPAndDestinataireCP(
                        expediteur, destinataire, destinataire, expediteur)
                .orElseGet(() -> {
                    ConversationPri c = new ConversationPri();
                    c.setExpediteurCP(expediteur);
                    c.setDestinataireCP(destinataire);
                    return conversationRepository.save(c);
                });

        Message message = new Message();
        message.setExpedi(expediteur);
        message.setConversation(conversation);
        message.setContenu(contenu);
        message.setDtEnvoi(LocalDateTime.now());

        messageRepository.save(message);
    }

    /**
     * avoir Message par ConversationId
     * 
     * @param conversationId
     * @return
     */
    public List<Message> getMessagesByConversationId(Long conversationId) {
        return messageRepository.findByConversationId(conversationId);
    }

    /**
     * envoyer message dans un group
     * 
     * @param expediteur
     * @param conversation
     * @param contenu
     * @return
     */
    public Message envoyerMessageGP(Utilisateur expediteur, Conversation conversation, String contenu) {
        Message message = new Message();
        message.setExpedi(expediteur);
        message.setContenu(contenu);
        message.setConversation(conversation);
        return messageRepository.save(message);
    }

    Pageable topFive = PageRequest.of(0, 5);

    /**
     * method pour avoir 5 message prive recent
     * 
     * @param utilisateur
     * @return
     */
    public List<Message> getRecentPriMessages(Utilisateur utilisateur) {
        return messageRepository.findPrivateMessagesForUser(utilisateur, topFive);
    }

    /**
     * method pour avoiri 5 message group recent
     * 
     * @param utilisateur
     * @return
     */
    public List<Message> getRecentGroupMessages(Utilisateur utilisateur) {
        return messageRepository.findGroupMessagesForUser(utilisateur, topFive);
    }

    /**
     * pour envoyer une message au groupe
     * 
     * @param groupeId
     * @param expediteur
     * @param contenu
     */
    public void envoyerMessageAuGroupe(Long groupeId, Utilisateur expediteur, String contenu) {
        ConversationGrp conversationGrp = conversationGrpRepository.findByGroupeCon_Id(groupeId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation de groupe introuvable"));

        Message message = new Message();
        message.setExpedi(expediteur);
        message.setContenu(contenu);
        message.setConversation(conversationGrp);

        messageRepository.save(message);
    }

    /**
     * pour avoit list de message de group
     * 
     * @param groupeId
     * @return
     */
    public List<Message> listerMessagesDuGroupe(Long groupeId) {
        ConversationGrp conversationGrp = conversationGrpRepository.findByGroupeCon_Id(groupeId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation de groupe introuvable"));
        return conversationGrp.getMessages();
    }
}
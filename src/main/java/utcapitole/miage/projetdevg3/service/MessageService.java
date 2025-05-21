package utcapitole.miage.projetdevg3.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import utcapitole.miage.projetdevg3.model.ConversationPri;
import utcapitole.miage.projetdevg3.model.Message;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.repository.ConversationPriRepository;
import utcapitole.miage.projetdevg3.repository.MessageRepository;

/**
 * Service pour la gestion des messages.
 * Contrôle l'envoi, la modification et la suppression des communications
 * utilisateur.
 */
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationPriRepository conversationRepository;

    public MessageService(ConversationPriRepository conversationRepository, MessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
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
}
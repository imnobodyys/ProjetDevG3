package utcapitole.miage.projetDevG3.Service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import utcapitole.miage.projetDevG3.Repository.ConversationPriRepository;
import utcapitole.miage.projetDevG3.Repository.MessageRepository;
import utcapitole.miage.projetDevG3.model.ConversationPri;
import utcapitole.miage.projetDevG3.model.Message;
import utcapitole.miage.projetDevG3.model.Utilisateur;

/**
 * Service pour la gestion des messages.
 * Contrôle l'envoi, la modification et la suppression des communications
 * utilisateur.
 */
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationPriRepository conversationRepository;

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
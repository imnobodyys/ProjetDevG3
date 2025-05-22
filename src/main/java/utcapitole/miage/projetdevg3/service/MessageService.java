package utcapitole.miage.projetdevg3.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import utcapitole.miage.projetdevg3.model.ConversationPri;
import utcapitole.miage.projetdevg3.model.ConversationGrp;
import utcapitole.miage.projetdevg3.model.Message;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.repository.MessageRepository;
import utcapitole.miage.projetdevg3.repository.ConversationGrpRepository;
import utcapitole.miage.projetdevg3.repository.ConversationPriRepository;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationPriRepository conversationPriRepository;
    private final ConversationGrpRepository conversationGrpRepository;
    private final ConversationService conversationService;

    private static final Pageable TOP_FIVE = PageRequest.of(0, 5);

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

    @Transactional
    public void envoyerOuCreerMessagePrive(Utilisateur expediteur, Utilisateur destinataire, String contenu) {
        ConversationPri conv = conversationService.findOrCreatePrivateConversation(expediteur, destinataire);
        envoyerMessagePrive(conv.getId(), expediteur, contenu);
    }

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

    public List<Message> getMessagesByConversationId(Long conversationId) {
        return messageRepository.findByConversationId(conversationId);
    }

    public List<Message> getRecentPrivateMessages(Utilisateur utilisateur) {
        return messageRepository.findPrivateMessagesForUser(utilisateur, TOP_FIVE);
    }

    public List<Message> getRecentGroupMessages(Utilisateur utilisateur) {
        return messageRepository.findGroupMessagesForUser(utilisateur, TOP_FIVE);
    }
}

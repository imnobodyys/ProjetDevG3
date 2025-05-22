package utcapitole.miage.projetdevg3.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import utcapitole.miage.projetdevg3.model.ConversationPri;
import utcapitole.miage.projetdevg3.model.ConversationGrp;
import utcapitole.miage.projetdevg3.model.Message;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.repository.ConversationPriRepository;
import utcapitole.miage.projetdevg3.repository.ConversationGrpRepository;
import utcapitole.miage.projetdevg3.repository.MessageRepository;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationPriRepository conversationPriRepository;
    private final ConversationGrpRepository conversationGrpRepository;
    private final MessageRepository messageRepository;
    private static final Pageable TOP_FIVE = PageRequest.of(0, 5);

    public List<ConversationPri> getPrivateConversationsWithRecentMessages(Utilisateur utilisateur) {
        List<ConversationPri> conversations = conversationPriRepository
                .findByExpediteurCPOrDestinataireCP(utilisateur, utilisateur);

        conversations.forEach(conv -> {
            List<Message> recentMessages = messageRepository
                    .findByConversationOrderByDtEnvoiDesc(conv, TOP_FIVE);
            conv.setRecentMessages(recentMessages);
        });

        return conversations;
    }

    public List<ConversationGrp> getGroupConversationsWithRecentMessages(Utilisateur utilisateur) {
        List<ConversationGrp> conversations = conversationGrpRepository.findByUtilisateur(utilisateur);

        conversations.forEach(conv -> {
            List<Message> recentMessages = messageRepository
                    .findByConversationOrderByDtEnvoiDesc(conv, TOP_FIVE);
            conv.setRecentMessages(recentMessages);
        });

        return conversations;
    }

    public List<Message> getMessagesForConversation(Long conversationId, Utilisateur utilisateur) {
        ConversationPri conversation = conversationPriRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation non trouvée"));

        if (!conversation.getExpediteurCP().equals(utilisateur)
                && !conversation.getDestinataireCP().equals(utilisateur)) {
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

    public ConversationPri findOrCreatePrivateConversation(Utilisateur u1, Utilisateur u2) {
        return conversationPriRepository.findConversationBetween(u1, u2)
                .orElseGet(() -> {
                    ConversationPri nouvelle = new ConversationPri();
                    nouvelle.setExpediteurCP(u1);
                    nouvelle.setDestinataireCP(u2);
                    return conversationPriRepository.save(nouvelle);
                });
    }

    public List<Message> getMessagesForGroup(Long groupeId) {
        ConversationGrp grp = conversationGrpRepository.findByGroupeCon_Id(groupeId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation de groupe introuvable"));
        return grp.getMessages();
    }
}

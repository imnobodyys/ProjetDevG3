package utcapitole.miage.projetDevG3.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utcapitole.miage.projetDevG3.Repository.ConversationRepository;

/**
 * Service générique pour la gestion des conversations.
 * Centralise les opérations communes à tous les types de conversations.
 */
@Service
public class ConversationService {
    @Autowired
    private ConversationRepository conversationRepository;

}
package utcapitole.miage.projetDevG3.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utcapitole.miage.projetDevG3.Repository.ConversationPriRepository;

/**
 * Service pour la gestion des conversations privées.
 * Permet des opérations CRUD sur les échanges entre utilisateurs individuels.
 */
@Service
public class ConversationPriService {
    @Autowired
    private ConversationPriRepository conversationPriRepository;

}
package utcapitole.miage.projetDevG3.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import utcapitole.miage.projetDevG3.Repository.ConversationGrpRepository;

/**
 * Service pour la gestion des conversations de groupe.
 * Offre des fonctionnalités de manipulation des discussions collectives.
 */
@Service
public class ConversationGrpService {
    /**
     * Référentiel pour les conversations de groupe.
     * Utilisé pour interagir avec la base de données.
     */
    @Autowired
    private ConversationGrpRepository conversationGrpRepository;

}
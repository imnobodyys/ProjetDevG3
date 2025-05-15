package utcapitole.miage.projetDevG3.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utcapitole.miage.projetDevG3.Repository.MessageRepository;

/**
 * Service pour la gestion des messages.
 * Contrôle l'envoi, la modification et la suppression des communications utilisateur.
 */
@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

}
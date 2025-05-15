package utcapitole.miage.projetDevG3.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utcapitole.miage.projetDevG3.Repository.CommentaireRepository;

/**
 * Service pour la gestion des commentaires.
 * Fournit des méthodes pour interagir avec la couche de persistance des commentaires.
 */
@Service
public class CommentaireService {
    @Autowired
    private CommentaireRepository commentaireRepository;

}

package utcapitole.miage.projetdevg3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import utcapitole.miage.projetdevg3.repository.CommentaireRepository;

/**
 * Service pour la gestion des commentaires.
 * 
 * Fournit des méthodes pour interagir avec la couche de persistance des
 * commentaires.
 */
@Service
public class CommentaireService {

    /**
     * Référentiel pour les commentaires.
     * Utilisé pour effectuer des opérations CRUD sur les commentaires.
     */
    @Autowired
    private CommentaireRepository commentaireRepository;

}

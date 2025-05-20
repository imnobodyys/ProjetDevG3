package utcapitole.miage.projetdevg3.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import utcapitole.miage.projetdevg3.model.Post;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.repository.MembreGroupeRepository;
import utcapitole.miage.projetdevg3.repository.PostRepository;


/**
 * Service pour la gestion des publications.
 * Régit la création, la mise à jour et la suppression des contenus
 * utilisateurs.
 */
@Service
public class PostService {

    /**
     * Référentiel pour les publications.
     * Utilisé pour effectuer des opérations CRUD sur les publications.
     */
    @Autowired
    private PostRepository postRepository;
    
    
}
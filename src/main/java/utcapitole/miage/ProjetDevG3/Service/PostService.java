package utcapitole.miage.projetDevG3.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utcapitole.miage.projetDevG3.Repository.PostRepository;

/**
 * Service pour la gestion des publications.
 * Régit la création, la mise à jour et la suppression des contenus utilisateurs.
 */
@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

}
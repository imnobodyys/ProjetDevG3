package utcapitole.miage.projetDevG3.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import utcapitole.miage.projetDevG3.Repository.MembreGroupeRepository;
import utcapitole.miage.projetDevG3.Repository.PostRepository;
import utcapitole.miage.projetDevG3.model.Post;

/**
 * Service pour la gestion des publications.
 * Régit la création, la mise à jour et la suppression des contenus utilisateurs.
 */
@Service
public class PostService {

    /**
     * Référentiel pour les publications.
     * Utilisé pour effectuer des opérations CRUD sur les publications.
     */
   @Autowired
    private PostRepository postRepository;

    @Autowired
    private MembreGroupeRepository membreGroupeRepository;

    public Post publierDansGroupe(Post post) {
        // Vérifier que l'auteur est membre du groupe
        boolean estMembre = membreGroupeRepository.existsByGroupeIdAndMembreId(
            post.getGroupe().getId(),
            post.getAuteur().getId()
        );

        if (!estMembre) {
            throw new IllegalArgumentException("L'utilisateur n'est pas membre du groupe");
        }

        return postRepository.save(post);
    }

    public List<Post> getPostsParGroupe(Long groupeId) {
        return postRepository.findByGroupeIdOrderByCreatedAtDesc(groupeId);
    }

}
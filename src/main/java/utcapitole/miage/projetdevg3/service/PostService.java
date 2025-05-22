package utcapitole.miage.projetdevg3.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import utcapitole.miage.projetdevg3.model.Commentaire;
import utcapitole.miage.projetdevg3.model.Post;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.model.VisibilitePost;
import utcapitole.miage.projetdevg3.repository.CommentaireRepository;
import utcapitole.miage.projetdevg3.repository.MembreGroupeRepository;
import utcapitole.miage.projetdevg3.repository.PostRepository;

/**
 * Service pour la gestion des publications.
 * Régit la création, la mise à jour et la suppression des contenus
 * utilisateurs.
 */
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentaireRepository commentaireRepository;

    public Post creerPost(Post post, Utilisateur auteur) {
        post.setAuteur(auteur);
        post.setCreatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    public List<Post> getPostsPublics() {
        return postRepository.findByVisibilite(VisibilitePost.PUBLIC);
    }

    public List<Post> getPostsParAuteur(Utilisateur auteur) {
        return postRepository.findByAuteur(auteur);
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post non trouvé"));
    }

    public void ajouterCommentaire(Long postId, String contenu, Utilisateur expediteur) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post non trouvé"));

        Commentaire commentaire = new Commentaire();
        commentaire.setContenu(contenu);
        commentaire.setExpediteur(expediteur);
        commentaire.setPost(post);
        commentaire.setDateEnvoi(LocalDateTime.now());

        commentaireRepository.save(commentaire);
    }

    @Transactional
    public boolean supprimerPost(Long postId, Utilisateur auteur) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post introuvable"));

        if (!post.getAuteur().equals(auteur)) {
            return false; // Non autorisé
        }

        postRepository.delete(post);
        return true;
    }

}

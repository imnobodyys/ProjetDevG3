package utcapitole.miage.projetdevg3.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import utcapitole.miage.projetdevg3.model.Post;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.model.VisibilitePost;

/**
 * PostRepository est une interface qui étend JpaRepository pour gérer les
 * opérations CRUD sur les entités Post.
 * Elle fournit des méthodes pour trouver des posts par auteur et par
 * visibilité.
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

      List<Post> findByGroupeIdOrderByCreatedAtDesc(Long groupeId);

      List<Post> findByAuteurOrderByCreatedAtDesc(Utilisateur auteur);

      List<Post> findByVisibilite(VisibilitePost visibilite);

      List<Post> findByAuteur(Utilisateur auteur);
}

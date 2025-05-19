package utcapitole.miage.projetDevG3.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import utcapitole.miage.projetDevG3.model.Post;

/**
 * PostRepository est une interface qui étend JpaRepository pour gérer les opérations CRUD sur les entités Post.
 * Elle fournit des méthodes pour trouver des posts par auteur et par visibilité.
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

      List<Post> findByGroupeIdOrderByCreatedAtDesc(Long groupeId);
}

package utcapitole.miage.projetdevg3.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import utcapitole.miage.projetdevg3.model.Post;
import utcapitole.miage.projetdevg3.model.Reaction;
import utcapitole.miage.projetdevg3.model.Utilisateur;

/**
 * ReactionRepository est une interface qui étend JpaRepository pour gérer les
 * opérations CRUD sur les entités Reaction.
 * Elle fournit des méthodes pour trouver des réactions par post et par
 * utilisateur.
 */
@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {

    List<Reaction> findByPost(Post post);

    List<Reaction> findByExpedient(Utilisateur expedient);
}
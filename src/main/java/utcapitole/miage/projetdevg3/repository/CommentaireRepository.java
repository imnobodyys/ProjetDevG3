package utcapitole.miage.projetdevg3.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import utcapitole.miage.projetdevg3.model.Commentaire;
import utcapitole.miage.projetdevg3.model.Post;
import utcapitole.miage.projetdevg3.model.Utilisateur;

/**
 * CommentaireRepository est une interface qui étend JpaRepository pour gérer
 * les opérations CRUD sur les entités Commentaire.
 * Elle fournit des méthodes pour trouver des commentaires par post et par
 * utilisateur.
 */
@Repository
public interface CommentaireRepository extends JpaRepository<Commentaire, Long> {

    /**
     * Méthode pour trouver tous les commentaires d'un post
     * 
     * @param post Le post dont on veut récupérer les commentaires
     * @return Une liste de commentaires associés au post
     */
    List<Commentaire> findByPost(Post post);

    // Méthode pour trouver tous les commentaires d'un utilisateur
    List<Commentaire> findByExpediteur(Utilisateur expediteur);

}

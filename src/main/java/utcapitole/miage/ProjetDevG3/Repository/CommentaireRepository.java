package utcapitole.miage.projetDevG3.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import utcapitole.miage.projetDevG3.model.Commentaire;
import utcapitole.miage.projetDevG3.model.Post;
import utcapitole.miage.projetDevG3.model.Utilisateur;

@Repository
public interface CommentaireRepository extends JpaRepository<Commentaire, Long> {
    // Méthode pour trouver tous les commentaires d'un post
    List<Commentaire> findByPost(Post post);

    // Méthode pour trouver tous les commentaires d'un utilisateur
    List<Commentaire> findByUtilisateur(Utilisateur utilisateur);

    

}

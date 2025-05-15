package utcapitole.miage.projetDevG3.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import utcapitole.miage.projetDevG3.model.Utilisateur;

/**
 * Interface Repository pour l'entité Utilisateur.
 * Permet les opérations CRUD standard grâce à JpaRepository,
 * ainsi que la recherche d'un utilisateur par son email.
 */
@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    /**
     * Recherche un utilisateur par son adresse email.
     * 
     * @param email l'adresse email de l'utilisateur à rechercher
     * @return un Optional contenant l'utilisateur si trouvé, sinon vide
     */
    Optional<Utilisateur> findByEmail(String email);
}
package utcapitole.miage.projetdevg3.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import utcapitole.miage.projetdevg3.model.Utilisateur;

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

    @Query("SELECT u FROM Utilisateur u WHERE " +
            "LOWER(u.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.prenom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")

    List<Utilisateur> searchByKeyword(@Param("keyword") String keyword);
}
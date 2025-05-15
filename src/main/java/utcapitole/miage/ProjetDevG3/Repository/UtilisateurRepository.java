package utcapitole.miage.projetDevG3.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import utcapitole.miage.projetDevG3.model.Utilisateur;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);

    // method pour rechercher utilisateur
    @Query("SELECT u FROM Utilisateur u WHERE " +
            "LOWER(u.nom) LIKE    OR " +
            "LOWER(u.prenom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")

    List<Utilisateur> searchByKeyword(@Param("keyword") String keyword);
}
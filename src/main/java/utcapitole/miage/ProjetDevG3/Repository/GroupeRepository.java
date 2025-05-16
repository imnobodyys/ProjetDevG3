package utcapitole.miage.projetDevG3.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import utcapitole.miage.projetDevG3.model.Groupe;
import utcapitole.miage.projetDevG3.model.StatutMembre;
import utcapitole.miage.projetDevG3.model.Utilisateur;

@Repository
public interface GroupeRepository extends JpaRepository<Groupe, Long> {
    
    boolean existsByNomIgnoreCase(String nom);

    // Méthode pour trouver un groupe par son ID
    Optional<Groupe> findById(Long id);
    
    List<Groupe> findByCreateur(Utilisateur createur);
   List<Groupe> findByMembresMembreAndMembresStatut(Utilisateur membre, StatutMembre statut);
}

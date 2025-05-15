package utcapitole.miage.projetDevG3.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import utcapitole.miage.projetDevG3.model.Groupe;
import utcapitole.miage.projetDevG3.model.StatutMembre;
import utcapitole.miage.projetDevG3.model.Utilisateur;

@Repository
public interface GroupeRepository extends JpaRepository<Groupe, Long> {
    // Méthode pour trouver un groupe par son ID
    Groupe findById(long id);
    
    List<Groupe> findByCreateur(Utilisateur createur);
   List<Groupe> findByMembresMembreAndMembresStatut(Utilisateur membre, StatutMembre statut);
}

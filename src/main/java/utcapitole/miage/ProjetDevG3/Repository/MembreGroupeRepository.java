package utcapitole.miage.projetDevG3.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import utcapitole.miage.projetDevG3.model.Groupe;
import utcapitole.miage.projetDevG3.model.MembreGroupe;
import utcapitole.miage.projetDevG3.model.Utilisateur;

public interface MembreGroupeRepository extends JpaRepository<MembreGroupe, Long> {
    // Méthode pour trouver un membre de groupe par son ID
    MembreGroupe findById(long id);

    // Méthode pour trouver tous les membres d'un groupe
    List<MembreGroupe> findByGroupe(Groupe groupe);

    // Méthode pour trouver tous les groupes d'un utilisateur
    List<MembreGroupe> findByUtilisateur(Utilisateur utilisateur);

}

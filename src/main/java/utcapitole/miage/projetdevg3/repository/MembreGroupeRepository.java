package utcapitole.miage.projetdevg3.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import utcapitole.miage.projetdevg3.model.Groupe;
import utcapitole.miage.projetdevg3.model.MembreGroupe;
import utcapitole.miage.projetdevg3.model.StatutMembre;
import utcapitole.miage.projetdevg3.model.Utilisateur;

/**
 * MembreGroupeRepository est une interface qui étend JpaRepository pour gérer
 * les opérations CRUD sur les entités MembreGroupe.
 * Elle fournit des méthodes pour trouver un membre de groupe par son ID,
 * trouver tous les membres d'un groupe,
 */
@Repository
public interface MembreGroupeRepository extends JpaRepository<MembreGroupe, Long> {

    boolean existsByGroupeIdAndMembreUtilisateurId(Long groupeId, Long membreUtilisateurId);

    // Méthode pour trouver tous les membres d'un groupe
    List<MembreGroupe> findByGroupe(Groupe groupe);

    // Méthode pour trouver tous les groupes d'un utilisateur
    List<MembreGroupe> findByMembreUtilisateur(Utilisateur membreUtilisateur);

    List<MembreGroupe> findByGroupeAndStatut(Groupe groupe, StatutMembre statut);

    List<MembreGroupe> findByMembreUtilisateurAndStatut(Utilisateur membreUtilisateur, StatutMembre statut);

    Optional<MembreGroupe> findByGroupeAndMembreUtilisateur(Groupe groupe, Utilisateur utilisateur);
}

package utcapitole.miage.projetdevg3.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import utcapitole.miage.projetdevg3.model.Evenement;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.model.VisibiliteEvenement;

/**
 * EvenementRepository est une interface qui étend JpaRepository pour gérer les
 * opérations CRUD sur les entités Evenement.
 * Elle fournit des méthodes pour trouver des événements par auteur et par
 * visibilité.
 */
@Repository
public interface EvenementRepository extends JpaRepository<Evenement, Long> {

    // Méthode pour trouver tous les événements d'un utilisateur
    List<Evenement> findByAuteur(Utilisateur auteur);

    // Méthode pour trouver tous les événements d'une visibilité
    List<Evenement> findByVisibilite(VisibiliteEvenement visibilite);

    // Méthode pour trouver un événement par son ID
    Evenement findById(long id);

}

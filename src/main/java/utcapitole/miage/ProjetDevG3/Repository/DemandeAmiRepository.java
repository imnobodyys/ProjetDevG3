package utcapitole.miage.projetDevG3.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import utcapitole.miage.projetDevG3.model.DemandeAmi;
import utcapitole.miage.projetDevG3.model.StatutDemande;
import utcapitole.miage.projetDevG3.model.Utilisateur;

public interface DemandeAmiRepository extends JpaRepository<DemandeAmi, Long> {
    // Méthode pour trouver toutes les demandes d'ami envoyées par un utilisateur
    List<DemandeAmi> findByExpediteur(Utilisateur expediteur);

    // Méthode pour trouver toutes les demandes d'ami reçues par un utilisateur
    List<DemandeAmi> findByDestinataire(Utilisateur destinataire);

    // Méthode pour trouver une demande d'ami par son statut
    List<DemandeAmi> findByStatut(StatutDemande statut);
}

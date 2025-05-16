package utcapitole.miage.projetDevG3.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import utcapitole.miage.projetDevG3.model.DemandeAmi;
import utcapitole.miage.projetDevG3.model.StatutDemande;
import utcapitole.miage.projetDevG3.model.Utilisateur;

@Repository
public interface DemandeAmiRepository extends JpaRepository<DemandeAmi, Long> {
    // Méthode pour trouver toutes les demandes d'ami envoyées par un utilisateur
    List<DemandeAmi> findByExpediteurAmi(Utilisateur expediteurAmi);

    // Méthode pour trouver toutes les demandes d'ami reçues par un utilisateur
    List<DemandeAmi> findByDestinataireAmi(Utilisateur destinataireAmi);

    // Méthode pour trouver une demande d'ami par son statut
    List<DemandeAmi> findByStatut(StatutDemande statut);

    /**
     * Méthode pour trouver demande ami
     * 
     * @param expediteurAmi
     * @param destinataireAmi
     * @return
     */
    boolean existsByExpediteurAmiAndDestinataireAmiOrDestinataireAmiAndExpediteurAmi(
            Long expediteur_id,
            Long destinataire_id);
}

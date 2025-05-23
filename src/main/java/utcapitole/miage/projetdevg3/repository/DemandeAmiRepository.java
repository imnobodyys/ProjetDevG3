package utcapitole.miage.projetdevg3.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import utcapitole.miage.projetdevg3.model.DemandeAmi;
import utcapitole.miage.projetdevg3.model.StatutDemande;
import utcapitole.miage.projetdevg3.model.Utilisateur;

/**
 * DemandeAmiRepository est une interface qui étend JpaRepository pour gérer les
 * opérations CRUD sur les entités DemandeAmi.
 * Elle fournit des méthodes pour trouver des demandes d'ami par expéditeur,
 * destinataire et statut.
 */
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
        @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM DemandeAmi d " +
                        "WHERE (d.expediteurAmi.id = :expediteur AND d.destinataireAmi.id = :destinataire) " +
                        "OR (d.expediteurAmi.id = :destinataire AND d.destinataireAmi.id = :expediteur)")
        boolean existsDemandeBetween(@Param("expediteur") Long expediteur, @Param("destinataire") Long destinataire);

        /**
         * 
         * @param destinataire
         * @param statut
         * @return list de demande ami et statut
         */
        List<DemandeAmi> findByDestinataireAmiAndStatut(Utilisateur destinataire, StatutDemande statut);

        /**
         * 
         * @param statut1
         * @param destinataire
         * @param statut2
         * @param expediteur
         * @return list des amis
         */
        List<DemandeAmi> findByStatutAndDestinataireAmiOrStatutAndExpediteurAmi(
                        StatutDemande statut1, Utilisateur destinataire,
                        StatutDemande statut2, Utilisateur expediteur);

        /**
         * pour supprimer ami
         * 
         * @param user1
         * @param user2
         */
        @Query("DELETE FROM DemandeAmi d WHERE " +
                        "(d.expediteurAmi = :user1 AND d.destinataireAmi = :user2) OR " +
                        "(d.expediteurAmi = :user2 AND d.destinataireAmi = :user1)")
        @Modifying
        void deleteAmitie(@Param("user1") Utilisateur user1, @Param("user2") Utilisateur user2);

        /**
         * pour avoir touts les amis
         * 
         * @param id
         * @return
         */
        @Query("SELECT d.expediteurAmi FROM DemandeAmi d WHERE d.destinataireAmi.id = :id AND d.statut = 'ACCEPTE'")
        List<Utilisateur> findExpediteursAmis(@Param("id") Long id);

        @Query("SELECT d.destinataireAmi FROM DemandeAmi d WHERE d.expediteurAmi.id = :id AND d.statut = 'ACCEPTE'")
        List<Utilisateur> findDestinatairesAmis(@Param("id") Long id);

}

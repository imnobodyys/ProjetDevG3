package utcapitole.miage.projetdevg3.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * classe DemandeAmi
 * Représente une demande d'ami entre deux utilisateurs
 */
@Entity
public class DemandeAmi {

    /**
     * Attributs
     * id : identifiant de la demande d'ami
     * dtEnvoi : date d'envoi de la demande
     * statut : statut de la demande (EN_ATTENTE, ACCEPTE, REFUSE)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dtEnvoi;

    /**
     * Enumération représentant les différents statuts d'une demande d'ami.
     * StatutDemande : EN_ATTENTE, ACCEPTE, REFUSE
     */
    @Enumerated(EnumType.STRING)
    private StatutDemande statut; // EN_ATTENTE, ACCEPTE, REFUSE

    /**
     * Relations
     * expediteurAmi : utilisateur qui envoie la demande d'ami
     * destinataireAmi : utilisateur qui reçoit la demande d'ami
     */
    @ManyToOne
    @JoinColumn(name = "expediteurAmi_id")
    private Utilisateur expediteurAmi;

    /**
     * Relation
     * destinataireAmi : utilisateur qui reçoit la demande d'ami
     */
    @ManyToOne
    @JoinColumn(name = "destinataireAmi_id")
    private Utilisateur destinataireAmi;

    // getters et setters
    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getDtEnvoi() {
        return dtEnvoi;
    }

    public void setDtEnvoi(LocalDateTime dtEnvoi) {
        this.dtEnvoi = dtEnvoi;
    }

    public StatutDemande getStatut() {
        return statut;
    }

    public void setStatut(StatutDemande statut) {
        this.statut = statut;
    }

    public Utilisateur getExpediteurAmi() {
        return expediteurAmi;
    }

    public void setExpediteurAmi(Utilisateur expediteur) {
        this.expediteurAmi = expediteur;
    }

    public Utilisateur getDestinataireAmi() {
        return destinataireAmi;
    }

    public void setDestinataireAmi(Utilisateur destinataire) {
        this.destinataireAmi = destinataire;
    }

}

package utcapitole.miage.projetDevG3.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
public class DemandeAmi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dtEnvoi;

    @Enumerated(EnumType.STRING)
    private StatutDemande statut; // EN_ATTENTE, ACCEPTE, REFUSE

    @ManyToOne
    @JoinColumn(name = "expediteurAmi_id")
    private Utilisateur expediteurAmi;

    @ManyToOne
    @JoinColumn(name = "destinataireAmi_id")
    private Utilisateur destinataireAmi;

    @PrePersist
    protected void onCreate() {
        dtEnvoi = LocalDateTime.now();
        statut = StatutDemande.EN_ATTENTE; 
    }

    // getters et setters
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

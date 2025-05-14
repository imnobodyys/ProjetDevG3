package utcapitole.miage.projetDevG3.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

public class DemandeAmi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dtEnvoi;

    @Enumerated(EnumType.STRING)
    private StatutDemande statut;

    @ManyToOne
    @JoinColumn(name = "expediteur_id")
    private Utilisateur expediteurAmi;

    @ManyToOne
    @JoinColumn(name = "destinataire_id")
    private Utilisateur destinataireAmi;

    @PrePersist
    protected void onCreate() {
        dtEnvoi = LocalDateTime.now();
        statut = StatutDemande.EN_ATTENTE; // 默认状态
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

    public Utilisateur getExpediteur() {
        return expediteurAmi;
    }

    public void setExpediteur(Utilisateur expediteur) {
        this.expediteurAmi = expediteur;
    }

    public Utilisateur getDestinataire() {
        return destinataireAmi;
    }

    public void setDestinataire(Utilisateur destinataire) {
        this.destinataireAmi = destinataire;
    }

}

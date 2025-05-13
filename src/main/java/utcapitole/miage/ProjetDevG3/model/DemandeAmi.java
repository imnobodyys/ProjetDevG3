package utcapitole.miage.ProjetDevG3.model;

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
    private Utilisateur expediteur;

    @ManyToOne
    @JoinColumn(name = "destinataire_id")
    private Utilisateur destinataire;

   
    

}

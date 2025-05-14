package utcapitole.miage.projetDevG3.model;

import jakarta.persistence.*;

@Entity
public class MembreGroupe {
    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private StatutMembre statut; // EN_ATTENTE, ACCEPTE, REFUSE

    @ManyToOne
    private Groupe groupe;

    @ManyToOne
    private Utilisateur membre;

    

    //getters et setters
    public Groupe getGroupe() {
        return groupe;
    }

    public void setGroupe(Groupe groupe) {
        this.groupe = groupe;
    }

    public Utilisateur getMembre() {
        return membre;
    }

    public void setMembre(Utilisateur membre) {
        this.membre = membre;
    }

    public StatutMembre getStatut() {
        return statut;
    }

    public void setStatut(StatutMembre statut) {
        this.statut = statut;
    }
}

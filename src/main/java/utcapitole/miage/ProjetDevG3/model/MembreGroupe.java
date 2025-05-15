package utcapitole.miage.projetDevG3.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class MembreGroupe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private StatutMembre statut; // EN_ATTENTE, ACCEPTE, REFUSE

    @ManyToOne
    private Groupe groupe;

    @ManyToOne
    private Utilisateur membre;

    // getters et setters
    public Groupe getGroupe() {
        return groupe;
    }

    public void setGroupe(Groupe groupe) {
        this.groupe = groupe;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getId() {
        return id;
    }
}

package utcapitole.miage.projetDevG3.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

<<<<<<< HEAD
/**
 * Classe MembreGroupe
 * Représente un membre d'un groupe
 * Chaque membre a un statut (EN_ATTENTE, ACCEPTE, REFUSE)
 */
=======
/** Javadoc */
>>>>>>> main
@Entity
public class MembreGroupe {

    /**
     * Attributs
     * id : identifiant du membre du groupe
     * statut : statut du membre (EN_ATTENTE, ACCEPTE, REFUSE)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Enumération représentant les différents statuts d'un membre de groupe.
     * StatutMembre : EN_ATTENTE, ACCEPTE, REFUSE
     */
    @Enumerated(EnumType.STRING)
    private StatutMembre statut; // EN_ATTENTE, ACCEPTE, REFUSE

    /**
     * Relations
     * groupe : groupe auquel appartient le membre
     * membre : utilisateur qui est membre du groupe
     */
    @ManyToOne
    private Groupe groupe;

    /**
     * Relation
     * membre : utilisateur qui est membre du groupe
     */
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

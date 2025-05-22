package utcapitole.miage.projetdevg3.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

/**
 * Classe MembreGroupe
 * Représente un membre d'un groupe
 * Chaque membre a un statut (EN_ATTENTE, ACCEPTE, REFUSE)
 */
@Entity
public class MembreGroupe {
    public MembreGroupe() {
    }

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
    private Utilisateur membreUtilisateur;

    public MembreGroupe(Utilisateur membre, Groupe groupe) {
        this.membreUtilisateur = membre;
        this.groupe = groupe;
        this.statut = StatutMembre.EN_ATTENTE;
    }

 
    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public StatutMembre getStatut() {
        return statut;
    }


    public void setStatut(StatutMembre statut) {
        this.statut = statut;
    }


    public Groupe getGroupe() {
        return groupe;
    }


    public void setGroupe(Groupe groupe) {
        this.groupe = groupe;
    }


    public Utilisateur getMembreUtilisateur() {
        return membreUtilisateur;
    }


    public void setMembreUtilisateur(Utilisateur membreUtilisateur) {
        this.membreUtilisateur = membreUtilisateur;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((statut == null) ? 0 : statut.hashCode());
        result = prime * result + ((groupe == null) ? 0 : groupe.hashCode());
        result = prime * result + ((membreUtilisateur == null) ? 0 : membreUtilisateur.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MembreGroupe other = (MembreGroupe) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (statut != other.statut)
            return false;
        if (groupe == null) {
            if (other.groupe != null)
                return false;
        } else if (!groupe.equals(other.groupe))
            return false;
        if (membreUtilisateur == null) {
            if (other.membreUtilisateur != null)
                return false;
        } else if (!membreUtilisateur.equals(other.membreUtilisateur))
            return false;
        return true;
    }
}

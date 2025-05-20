
package utcapitole.miage.projetdevg3.model;

import java.time.LocalDateTime;
import java.util.List;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;


/**
 * Classe Groupe
 * Représente un groupe d'utilisateurs
 * Chaque groupe a un créateur et peut avoir plusieurs membres
 */
@Entity
public class Groupe {
    /**
     * Attributs
     * id : identifiant du groupe
     * nom : nom du groupe
     * description : description du groupe
     * dtCreation : date de création du groupe
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

<<<<<<< HEAD
    @NotBlank
    @Size
=======
>>>>>>> main
    private String nom;

    @NotBlank
    @Size
    private String description;
    private LocalDateTime dtCreation;

    public enum TypeGroupe {
        PUBLIC,
        PRIVE
    }

    @Enumerated(EnumType.STRING)
    private TypeGroupe type = TypeGroupe.PUBLIC;

    public TypeGroupe getType() {
        return type;
    }

    private boolean actif = true;

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public boolean isActif() {
        return actif;
    }

    /**
     * Relations
     * createur : utilisateur qui a créé le groupe
     * conversationGrp : conversation de groupe associée au groupe
     * membres : liste des membres du groupe
     */
    @ManyToOne
    private Utilisateur createur;

    /**
     * Enumération représentant les différents types de groupes.
     * TypeGroupe : PUBLIC, PRIVE
     */
    @OneToOne(mappedBy = "groupeCon")
    private ConversationGrp conversationGrp;

    /**
     * Relation
     * membres : liste des membres du groupe
     */
    @OneToMany(mappedBy = "groupe", cascade = CascadeType.ALL)
    private List<MembreGroupe> membres;

    /**
     * Constructeur par défaut
     * Initialise la date de création à la date actuelle
     */
    @PrePersist
    protected void onCreate() {
        this.dtCreation = LocalDateTime.now();
    }

    public void setConversationGrp(ConversationGrp conversationGrp) {
        if (conversationGrp == null) {
            if (this.conversationGrp != null) {
                this.conversationGrp.setGroupeCon(null);
            }
        } else {
            conversationGrp.setGroupeCon(this);
        }
        this.conversationGrp = conversationGrp;
    }

    /**
     * Getters et Setters
     * id : identifiant du groupe
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMembres(List<MembreGroupe> membres) {
        this.membres = membres;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDtCreation() {
        return dtCreation;
    }

    public void setDtCreation(LocalDateTime dtCreation) {
        this.dtCreation = dtCreation;
    }

    public Utilisateur getCreateur() {
        return createur;
    }

    public void setCreateur(Utilisateur createur) {
        this.createur = createur;
    }

    public ConversationGrp getConversationGrp() {
        return conversationGrp;
    }

    public List<MembreGroupe> getMembres() {
        return membres;
    }

    public void addMembre(MembreGroupe membre) {
        if (membre != null && !membres.contains(membre)) {
            membres.add(membre);
            membre.setGroupe(this);
        }
    }

    public void removeMembre(MembreGroupe membre) {
        if (membre != null) {
            membres.remove(membre);
            membre.setGroupe(null);
        }
    }

    public void setType(TypeGroupe type) {
        this.type = type;
    }

}

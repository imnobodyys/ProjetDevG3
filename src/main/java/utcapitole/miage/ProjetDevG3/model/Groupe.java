
package utcapitole.miage.projetDevG3.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;

@Entity
public class Groupe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String description;
    private LocalDateTime dtCreation;

    @ManyToOne
    private Utilisateur createur;

    @OneToOne(mappedBy = "groupeCon")
    private ConversationGrp conversationGrp;

    @OneToMany(mappedBy = "groupe", cascade = CascadeType.ALL)
    private List<MembreGroupe> membres;

    @PrePersist
    protected void onCreate() {
        this.dtCreation = LocalDateTime.now();
    }

    // getters et setters
    public Long getId() {
        return id;
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

}

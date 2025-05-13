
package utcapitole.miage.projetDevG3.model;

import java.util.List;

import jakarta.persistence.*;

@Entity
public class Groupe {
    @Id
    @GeneratedValue
    private Long id;

    private String nom;

    @ManyToOne
    private Utilisateur createur;

    @OneToOne(mappedBy = "groupeCon")
    private ConversationGrp conversationGrp;

    @OneToMany(mappedBy = "groupe", cascade = CascadeType.ALL)
    private List<MembreGroupe> membres;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
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
        this.conversationGrp = conversationGrp;
    }

    public List<MembreGroupe> getMembres() {
        return membres;
    }

    public void setMembres(List<MembreGroupe> membres) {
        this.membres = membres;
    }
}

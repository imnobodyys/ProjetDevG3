package utcapitole.miage.projetDevG3.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;


/**
 * Classe ConversationGrp
 * Représente une conversation de groupe
 */
@Entity
public class ConversationGrp extends Conversation {
    
    /**
     * Attributs
     * id : identifiant de la conversation
     * groupeCon : groupe associé à la conversation
     */
    @OneToOne
    @JoinColumn(name = "group_id", unique = true)
    private Groupe groupeCon;

    // getters et setters
    public Groupe getGroupeCon() {
        return groupeCon;
    }

    public void setGroupeCon(Groupe groupeCon) {
        this.groupeCon = groupeCon;
    }
}

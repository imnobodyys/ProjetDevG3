package utcapitole.miage.projetDevG3.model;

import jakarta.persistence.*;

/** Javadoc */
@Entity
public class ConversationGrp extends Conversation {
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

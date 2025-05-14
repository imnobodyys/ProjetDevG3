package utcapitole.miage.projetDevG3.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class ConversationGrp extends Conversation {
    @OneToOne
    @JoinColumn(name = "group id", unique = true)
    private Groupe groupeCon;

    public Groupe getGroupeCon() {
        return groupeCon;
    }

    public void setGroupeCon(Groupe groupeCon) {
        this.groupeCon = groupeCon;
    }
}

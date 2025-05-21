package utcapitole.miage.projetdevg3.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;

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

    @Transient
    private List<Message> recentMessages;

    /**
     * getters et setters
     * getId : retourne l'identifiant de la conversationGrp
     */

    public List<Message> getRecentMessages() {
        return recentMessages;
    }

    public void setRecentMessages(List<Message> recentMessages) {
        this.recentMessages = recentMessages;
    }

    public Groupe getGroupeCon() {
        return groupeCon;
    }

    public void setGroupeCon(Groupe groupeCon) {
        this.groupeCon = groupeCon;
    }
}

package utcapitole.miage.projetdevg3.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;

/**
 * Classe ConversationPri
 * Représente une conversation privée entre deux utilisateurs
 */
@Entity
public class ConversationPri extends Conversation {

    /**
     * Attributs
     * expediteurCP : utilisateur qui envoie le message
     * destinataireCP : utilisateur qui reçoit le message
     */
    @ManyToOne
    @JoinColumn(name = "expediteur_id", nullable = false)
    private Utilisateur expediteurCP;

    /**
     * Relation
     * destinataireCP : utilisateur qui reçoit le message
     */
    @ManyToOne
    @JoinColumn(name = "destinataire_id", nullable = false)
    private Utilisateur destinataireCP;

    // getters et setters
    public Utilisateur getExpediteurCP() {
        return expediteurCP;
    }

    @Transient
    private List<Message> recentMessages;

    public List<Message> getRecentMessages() {
        return recentMessages;
    }

    public void setRecentMessages(List<Message> recentMessages) {
        this.recentMessages = recentMessages;
    }

    public void setExpediteurCP(Utilisateur expediteur) {
        this.expediteurCP = expediteur;
    }

    public Utilisateur getDestinataireCP() {
        return destinataireCP;
    }

    public void setDestinataireCP(Utilisateur destinataire) {
        this.destinataireCP = destinataire;
    }

}

package utcapitole.miage.projetDevG3.model;

import jakarta.persistence.*;

/** Javadoc */
@Entity
public class ConversationPri extends Conversation {
    @ManyToOne
    @JoinColumn(name = "expediteur_id", nullable = false)
    private Utilisateur expediteurCP;

    @ManyToOne
    @JoinColumn(name = "destinataire_id", nullable = false)
    private Utilisateur destinataireCP;

    // getters et setters
    public Utilisateur getExpediteurCP() {
        return expediteurCP;
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

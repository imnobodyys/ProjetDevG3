package utcapitole.miage.projetDevG3.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;

/**
 * Classe Message
 * Représente un message envoyé par un utilisateur dans une conversation
 */
@Entity
public class Message {
    /**
     * Attributs
     * id : identifiant du message
     * contenu : contenu du message
     * dtEnvoi : date d'envoi du message
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contenu;

    private LocalDateTime dtEnvoi;

    /**
     * Relations
     * expediteur : utilisateur qui a envoyé le message
     * conversation : conversation dans laquelle le message a été envoyé
     */
    @ManyToOne(optional = false)
    private Utilisateur expedi;

    /**
     * Relation
     * conversation : conversation dans laquelle le message a été envoyé
     */
    @ManyToOne(optional = false)
    private Conversation conversation;

    /**
     * Constructeur par défaut
     * Initialise la date d'envoi à la date actuelle
     */
    @PrePersist
    public void setDateEnvoi() {
        this.dtEnvoi = LocalDateTime.now();
    }

    // getters et setters
    public Long getId() {
        return id;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public LocalDateTime getDtEnvoi() {
        return dtEnvoi;
    }

    public void setDtEnvoi(LocalDateTime dtEnvoi) {
        this.dtEnvoi = dtEnvoi;
    }

    public Utilisateur getExpedi() {
        return expedi;
    }

    public void setExpedi(Utilisateur expedi) {
        this.expedi = expedi;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public void setId(long l) {
        this.id = l;
    }

}

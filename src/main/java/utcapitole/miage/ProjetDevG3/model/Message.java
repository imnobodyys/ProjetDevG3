package utcapitole.miage.projetDevG3.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
public class Message {
    @Id
    @GeneratedValue
    private long id;

    private String contenu;

    private LocalDateTime dtEnvoi;

    @ManyToOne(optional = false)
    private Utilisateur expedi;

    @ManyToOne(optional = false)
    private Conversation conversation;

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

    @PrePersist
    public void setDateEnvoi() {
        this.dtEnvoi = LocalDateTime.now();
    }
}

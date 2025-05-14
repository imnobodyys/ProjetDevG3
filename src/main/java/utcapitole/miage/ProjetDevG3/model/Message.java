package utcapitole.miage.projetDevG3.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;

@Entity
public class Message {
    @Id
    @GeneratedValue
    private Long id;

    private String contenu;

    private LocalDateTime dtEnvoi;

    @ManyToOne(optional = false)
    private Utilisateur expedi;

    @ManyToOne(optional = false)
    private Conversation conversation;


    @PrePersist
    public void setDateEnvoi() {
        this.dtEnvoi = LocalDateTime.now();
    }



     //getters et setters
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
    
}

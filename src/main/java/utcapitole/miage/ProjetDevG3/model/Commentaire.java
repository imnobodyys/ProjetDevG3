package utcapitole.miage.ProjetDevG3.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Commentaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contenu;
    private LocalDateTime dateEnvoi;

    @ManyToOne
    @JoinColumn(name = "expedient_id")
    private Utilisateur expediteur;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    

    
    //getters et setters
    public Long getId() {
        return id;
    }

    public Utilisateur getExpediteur() {
        return expediteur;
    }

    public String getContenu() {
        return contenu;
    }

    public Post getPost() {
        return post;
    }

    public LocalDateTime getDateEnvoi() {
        return dateEnvoi;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setExpediteur(Utilisateur expediteur) {
        this.expediteur = expediteur;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public void setDateEnvoi(LocalDateTime dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }
    
    
}

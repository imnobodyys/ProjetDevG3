package utcapitole.miage.projetdevg3.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * Classe Commentaire
 * Représente un commentaire sur un post
 */
@Entity
public class Commentaire {

    /**
     * Attributs
     * id : identifiant du commentaire
     * contenu : contenu du commentaire
     * dtEnvoi : date d'envoi du commentaire
     * expediteur : utilisateur qui a envoyé le commentaire
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contenu;
    private LocalDateTime dtEnvoi;

    /**
     * Relations
     * expediteur : utilisateur qui a envoyé le commentaire
     * post : post sur lequel le commentaire a été fait
     */
    @ManyToOne
    @JoinColumn(name = "expedient_id")
    private Utilisateur expediteur;

    /**
     * Relation
     * post : post sur lequel le commentaire a été fait
     */
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    /**
     * getters et setters
     * getId : retourne l'identifiant du commentaire
     */
    public Long getId() {
        return id;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public LocalDateTime getDateEnvoi() {
        return dtEnvoi;
    }

    public void setDateEnvoi(LocalDateTime dtEnvoi) {
        this.dtEnvoi = dtEnvoi;
    }

    public Utilisateur getExpediteur() {
        return expediteur;
    }

    public void setExpediteur(Utilisateur expediteur) {
        this.expediteur = expediteur;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

}

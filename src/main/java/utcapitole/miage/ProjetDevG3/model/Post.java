package utcapitole.miage.projetDevG3.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

<<<<<<< HEAD
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

/**
 * Classe Post
 * Représente un post sur le réseau social
 */
=======
import jakarta.persistence.*;

/** Javadoc */
>>>>>>> main
@Entity
public class Post {
    /**
     * Attributs
     * id : identifiant du post
     * contenu : contenu du post
     * dtPublication : date de publication du post
     * visibilite : visibilité du post (publique ou privée)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contenu;
    private LocalDateTime dtPublication;

    /**
     * Enumération représentant les différentes visibilités d'un post.
     * VisibilitePost : PUBLIQUE, PRIVE
     */
    @Enumerated(EnumType.STRING)
    private VisibilitePost visibilite;

    /**
     * Relations
     * auteur : utilisateur qui a créé le post
     * originalPost : post d'origine (si le post est un repost)
     * reposts : liste des reposts du post
     * commentaires : liste des commentaires sur le post
     */
    @ManyToOne
    @JoinColumn(name = "auteur_id")
    private Utilisateur auteur;

    /**
     * Relation
     * originalPost : post d'origine (si le post est un repost)
     */
    @ManyToOne
    @JoinColumn(name = "original_post_id")
    private Post originalPost;

    /**
     * Relation
     * reposts : liste des reposts du post
     */
    @OneToMany(mappedBy = "originalPost")
    private List<Post> reposts = new ArrayList<>();

    /**
     * Relation
     * commentaires : liste des commentaires sur le post
     */
    @OneToMany(mappedBy = "post")
    private List<Commentaire> commentaires = new ArrayList<>();

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

    public LocalDateTime getDtPublication() {
        return dtPublication;
    }

    public void setDtPublication(LocalDateTime dtPublication) {
        this.dtPublication = dtPublication;
    }

    public VisibilitePost getVisibilite() {
        return visibilite;
    }

    public void setVisibilite(VisibilitePost visibilite) {
        this.visibilite = visibilite;
    }

    public Utilisateur getAuteur() {
        return auteur;
    }

    public void setAuteur(Utilisateur auteur) {
        this.auteur = auteur;
    }

    public Post getOriginalPost() {
        return originalPost;
    }

    public void setOriginalPost(Post originalPost) {
        this.originalPost = originalPost;
    }

    public List<Post> getReposts() {
        return reposts;
    }

    public void addRepost(Post repost) {
        this.reposts.add(repost);
        repost.setOriginalPost(this);
    }

    public void removeRepost(Post repost) {
        this.reposts.remove(repost);
        repost.setOriginalPost(null);
    }

    public List<Commentaire> getCommentaires() {
        return commentaires;
    }

    public void addCommentaire(Commentaire commentaire) {
        this.commentaires.add(commentaire);
        commentaire.setPost(this);
    }

    public void removeCommentaire(Commentaire commentaire) {
        this.commentaires.remove(commentaire);
        commentaire.setPost(null);
    }

}

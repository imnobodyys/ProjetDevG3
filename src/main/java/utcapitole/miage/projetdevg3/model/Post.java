package utcapitole.miage.projetdevg3.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;

/**
 * Classe Post
 * Représente un post sur le réseau social
 */
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

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void setCreatedAt() {
        this.createdAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

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
     * groupe : groupe auquel appartient le post
     */
    @ManyToOne
    @JoinColumn(name = "groupe_id")
    private Groupe groupe;

    public Groupe getGroupe() {
        return groupe;
    }

    public void setGroupe(Groupe groupe) {
        this.groupe = groupe;
    }

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

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Reaction> reactions = new ArrayList<>();

    public List<Reaction> getReactions() {
        return reactions;
    }

    public void setReactions(List<Reaction> reactions) {
        this.reactions = reactions;
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setReposts(List<Post> reposts) {
        this.reposts = reposts;
    }

    public void setCommentaires(List<Commentaire> commentaires) {
        this.commentaires = commentaires;
    }

}

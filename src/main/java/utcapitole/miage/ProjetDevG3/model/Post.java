package utcapitole.miage.ProjetDevG3.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contenu;
    private LocalDateTime datePublication;

    @Enumerated(EnumType.STRING)
    private VisibilitePost visibilite;

    @ManyToOne
    @JoinColumn(name = "auteur_id")
    private Utilisateur auteur;

    @ManyToOne
    @JoinColumn(name = "original_post_id") 
    private Post originalPost;

    @OneToMany(mappedBy = "originalPost")
    private List<Post> reposts = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<Commentaire> commentaires = new ArrayList<>();


    //getters et setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public LocalDateTime getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(LocalDateTime datePublication) {
        this.datePublication = datePublication;
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

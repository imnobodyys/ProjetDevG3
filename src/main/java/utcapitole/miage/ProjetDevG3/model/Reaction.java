package utcapitole.miage.projetDevG3.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * Classe Reaction
 * Représente une réaction à un post
 */
@Entity
public class Reaction {

    /**
     * Attributs
     * id : identifiant de la réaction
     * dtEnvoi : date d'envoi de la réaction
     * type : type de la réaction (J'aime, J'adore, etc.)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dtEnvoi;

    /**
     * Enumération représentant les différents types de réactions.
     * TypeReaction : J_AIME, J_ADORE, RIRE, TRISTE, ENERVEMENT
     */
    @Enumerated(EnumType.STRING)
    private TypeReaction type;

    /**
     * Relations
     * expedient : utilisateur qui a envoyé la réaction
     * post : post auquel la réaction est associée
     */
    @ManyToOne
    @JoinColumn(name = "expedient_id")
    private Utilisateur expedient;

    /**
     * Relation
     * post : post auquel la réaction est associée
     */
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    
    // getters et setters
    public Long getId() {
        return id;
    }

    public LocalDateTime getDtEnvoi() {
        return dtEnvoi;
    }

    public void setDtEnvoi(LocalDateTime dtEnvoi) {
        this.dtEnvoi = dtEnvoi;
    }

    public TypeReaction getType() {
        return type;
    }

    public void setType(TypeReaction type) {
        this.type = type;
    }

    public Utilisateur getExpedient() {
        return expedient;
    }

    public void setExpedient(Utilisateur expedient) {
        this.expedient = expedient;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

}

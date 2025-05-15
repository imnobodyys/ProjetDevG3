package utcapitole.miage.projetDevG3.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

/** Javadoc */
@Entity
public class Reaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dtEnvoi;

    @Enumerated(EnumType.STRING)
    private TypeReaction type;

    @ManyToOne
    @JoinColumn(name = "expedient_id")
    private Utilisateur expedient;

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

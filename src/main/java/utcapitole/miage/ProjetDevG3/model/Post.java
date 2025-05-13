package utcapitole.miage.projetDevG3.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Post {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Utilisateur auteur;

    private String contenu;

    @ManyToOne
    private Post originalPost;

    @ManyToOne
    private Groupe groupe;

    private boolean estBrouillon = false;

    private LocalDateTime datePublication;

}

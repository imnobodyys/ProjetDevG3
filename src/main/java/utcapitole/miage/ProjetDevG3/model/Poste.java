package utcapitole.miage.ProjetDevG3.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Poste {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Utilisateur auteur;

    private String contenu;

    @ManyToOne
    private Poste originalPost;

    @ManyToOne
    private Groupe groupe;

    private boolean estBrouillon = false;

    private LocalDateTime datePublication;

    // Getters / Setters 或用 Lombok
}

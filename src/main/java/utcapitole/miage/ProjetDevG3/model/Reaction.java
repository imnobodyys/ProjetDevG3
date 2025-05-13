package utcapitole.miage.ProjetDevG3.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

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

   
}

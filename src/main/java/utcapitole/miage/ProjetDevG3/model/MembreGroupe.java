package utcapitole.miage.ProjetDevG3.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class MembreGroupe {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Groupe groupe;

    @ManyToOne
    private Utilisateur membre;

    @Enumerated(EnumType.STRING)
    private StatutMembre statut; // EN_ATTENTE, ACCEPTE, REFUSE
}

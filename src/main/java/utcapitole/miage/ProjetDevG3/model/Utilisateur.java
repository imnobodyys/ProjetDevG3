package utcapitole.miage.ProjetDevG3.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    private String prenom;

    @Column(unique = true, nullable = false)
    private String email;

    private String mdp;

    private LocalDateTime dtInscription;

}


package utcapitole.miage.ProjetDevG3.model;

import jakarta.persistence.*;

@Entity
public class Groupe {
    @Id
    @GeneratedValue
    private Long id;

    private String nom;
    private String description;
    private String theme;

    @ManyToOne
    private Utilisateur createur;
}
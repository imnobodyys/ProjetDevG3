package utcapitole.miage.projetDevG3.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Evenement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;
    private String description;
    private String contenu;
    private LocalDateTime datePublication;

    @Enumerated(EnumType.STRING)
    private VisibiliteEvenement visibilite;

    @ManyToOne
    @JoinColumn(name = "auteur_id")
    private Utilisateur auteur;

    @ManyToMany(mappedBy = "evenements")
    private List<Utilisateur> participants = new ArrayList<>();

    // getters et setters
    public Long getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public VisibiliteEvenement getVisibilite() {
        return visibilite;
    }

    public void setVisibilite(VisibiliteEvenement visibilite) {
        this.visibilite = visibilite;
    }

    public Utilisateur getAuteur() {
        return auteur;
    }

    public void setAuteur(Utilisateur auteur) {
        this.auteur = auteur;
    }

    public List<Utilisateur> getParticipants() {
        return participants;
    }

    public void addParticipant(Utilisateur participant) {
        this.participants.add(participant);
        participant.getEvenements().add(this);
    }

    public void removeParticipant(Utilisateur participant) {
        this.participants.remove(participant);
        participant.getEvenements().remove(this);
    }

}

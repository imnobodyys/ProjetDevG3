package utcapitole.miage.projetDevG3.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

/**
 * Classe Evenement
 * Représente un événement
 * Chaque événement a un auteur et peut avoir plusieurs participants
 * Chaque participant peut participer à plusieurs événements
 */
@Entity
public class Evenement {
    /**
     * Attributs
     * id : identifiant de l'événement
     * titre : titre de l'événement
     * description : description de l'événement
     * contenu : contenu de l'événement
     * dtPublication : date de publication de l'événement
     * visibilite : visibilité de l'événement (publique ou privée)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;
    private String description;
    private String contenu;
    private LocalDateTime dtPublication;

    /**
     * Enumération représentant les différentes visibilités d'un événement.
     * VisibiliteEvenement : PUBLIQUE, PRIVE
     */
    @Enumerated(EnumType.STRING)
    private VisibiliteEvenement visibilite;

    /**
     * Relations
     * auteur : utilisateur qui a créé l'événement
     * participants : liste des utilisateurs qui participent à l'événement
     */
    @ManyToOne
    @JoinColumn(name = "auteur_id")
    private Utilisateur auteur;

    /**
     * Relation
     * participants : liste des utilisateurs qui participent à l'événement
     */
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
        return dtPublication;
    }

    public void setDatePublication(LocalDateTime dtPublication) {
        this.dtPublication = dtPublication;
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
        if (participant != null && !participants.contains(participant)) {
            participants.add(participant);
            participant.addEvenement(this);
        }
    }

    public void removeParticipant(Utilisateur participant) {
        if (participant != null) {
            participants.remove(participant);
            participant.removeEvenement(this);
        }
    }

}

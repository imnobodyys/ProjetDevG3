package utcapitole.miage.projetDevG3.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

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

    @OneToMany(mappedBy = "auteur")
    private List<Post> posts = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "utilisateur_evenement", joinColumns = @JoinColumn(name = "utilisateur_id"), inverseJoinColumns = @JoinColumn(name = "evenement_id"))
    private List<Evenement> evenements = new ArrayList<>();

    @OneToMany(mappedBy = "expediteur")
    private List<ConversationPri> conversationsCommeA;

    @OneToMany(mappedBy = "destinataire")
    private List<ConversationPri> conversationsCommeB;

    @OneToMany(mappedBy = "membre", cascade = CascadeType.ALL)
    private List<MembreGroupe> groupes;

    // getters et setters
    public List<ConversationPri> getConversationsCommeA() {
        return conversationsCommeA;
    }

    public void setConversationsCommeA(List<ConversationPri> conversationsCommeA) {
        this.conversationsCommeA = conversationsCommeA;
    }

    public List<ConversationPri> getConversationsCommeB() {
        return conversationsCommeB;
    }

    public void setConversationsCommeB(List<ConversationPri> conversationsCommeB) {
        this.conversationsCommeB = conversationsCommeB;
    }

    public List<MembreGroupe> getGroupes() {
        return groupes;
    }

    public void setGroupes(List<MembreGroupe> groupes) {
        this.groupes = groupes;
    }

    public List<Evenement> getEvenements() {
        return evenements;
    }

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    public LocalDateTime getDtInscription() {
        return dtInscription;
    }

    public void setDtInscription(LocalDateTime dtInscription) {
        this.dtInscription = dtInscription;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void addPost(Post post) {
        this.posts.add(post);

    }

    public void addEvenement(Evenement evenement) {
        this.evenements.add(evenement);
        evenement.getParticipants().add(this);
    }

    public void removeEvenement(Evenement evenement) {
        this.evenements.remove(evenement);
        evenement.getParticipants().remove(this);
    }
}

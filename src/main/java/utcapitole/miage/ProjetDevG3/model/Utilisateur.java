package utcapitole.miage.projetdevg3.model;

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

/**
 * Classe Utilisateur
 * Représente un utilisateur du système
 * Chaque utilisateur peut avoir plusieurs posts, événements, commentaires,
 * conversations, groupes et demandes d'amis
 */
@Entity
public class Utilisateur {

    /**
     * Attributs
     * id : identifiant de l'utilisateur
     * nom : nom de l'utilisateur
     * prenom : prénom de l'utilisateur
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;

    /**
     * Attributs
     * email : adresse email de l'utilisateur
     * mdp : mot de passe de l'utilisateur
     * dtInscription : date d'inscription de l'utilisateur
     */
    @Column(unique = true, nullable = false)
    private String email;

    private String mdp;

    private LocalDateTime dtInscription;

    /**
     * Relations
     * posts : liste des posts de l'utilisateur
     */
    public Utilisateur(String nom, String prenom, String email, String mdp) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.mdp = mdp;
        this.dtInscription = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "auteur")
    private List<Post> posts = new ArrayList<>();

    /**
     * Relation
     * evenements : liste des événements auxquels l'utilisateur participe
     */
    @ManyToMany
    @JoinTable(name = "utilisateur_evenement", joinColumns = @JoinColumn(name = "utilisateur_id"), inverseJoinColumns = @JoinColumn(name = "evenement_id"))
    private List<Evenement> evenements = new ArrayList<>();

    /**
     * Relation
     * commentaires : liste des commentaires de l'utilisateur
     */
    @OneToMany(mappedBy = "expediteur")
    private List<Commentaire> commentaires = new ArrayList<>();

    /**
     * Relation
     * conversations : liste des conversations privées de l'utilisateur
     */
    @OneToMany(mappedBy = "expediteurCP")
    private List<ConversationPri> conversationsCommeA = new ArrayList<>();

    /**
     * Relation
     * conversations : liste des conversations privées de l'utilisateur
     */
    @OneToMany(mappedBy = "destinataireCP")
    private List<ConversationPri> conversationsCommeB = new ArrayList<>();

    /**
     * Relation
     * groupes : liste des groupes auxquels l'utilisateur appartient
     */
    @OneToMany(mappedBy = "membre", cascade = CascadeType.ALL)
    private List<MembreGroupe> groupes = new ArrayList<>();

    /**
     * Relation
     * demandes d'amis : liste des demandes d'amis envoyées et reçues par
     * l'utilisateur
     */
    @OneToMany(mappedBy = "expediteurAmi")
    private List<DemandeAmi> demandeAmiExp = new ArrayList<>();

    /**
     * Relation
     * demandes d'amis : liste des demandes d'amis envoyées et reçues par
     * l'utilisateur
     */
    @OneToMany(mappedBy = "destinataireAmi")
    private List<DemandeAmi> demandeAmiDes = new ArrayList<>();

    /**
     * Relation
     * messages : liste des messages envoyés par l'utilisateur
     */
    @OneToMany(mappedBy = "expedi", cascade = CascadeType.ALL)
    private List<Message> messages = new ArrayList<>();

    // getters et setters
    public Utilisateur() {
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
        if (post != null && !posts.contains(post)) {
            posts.add(post);
            post.setAuteur(this);
        }
    }

    public void removePost(Post post) {
        if (post != null) {
            posts.remove(post);
            post.setAuteur(null);
        }
    }

    public List<Evenement> getEvenements() {
        return evenements;
    }

    public void addEvenement(Evenement evenement) {
        if (evenement != null && !evenements.contains(evenement)) {
            evenements.add(evenement);
            evenement.addParticipant(this);
        }
    }

    public void removeEvenement(Evenement evenement) {
        if (evenement != null) {
            evenements.remove(evenement);
            evenement.removeParticipant(this);
        }
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Commentaire> getCommentaires() {
        return commentaires;
    }

    public void addCommentaire(Commentaire commentaire) {
        if (commentaire != null && !commentaires.contains(commentaire)) {
            commentaires.add(commentaire);
            commentaire.setExpediteur(this);
        }
    }

    public void removeCommentaire(Commentaire commentaire) {
        if (commentaire != null) {
            commentaires.remove(commentaire);
            commentaire.setExpediteur(null);
        }
    }

    public List<ConversationPri> getConversationsCommeA() {
        return conversationsCommeA;
    }

    public void addConversationCommeA(ConversationPri conversation) {
        if (conversation != null && !conversationsCommeA.contains(conversation)) {
            conversationsCommeA.add(conversation);
            conversation.setExpediteurCP(this);
        }
    }

    public void removeConversationCommeA(ConversationPri conversation) {
        if (conversation != null) {
            conversationsCommeA.remove(conversation);
            conversation.setExpediteurCP(null);
        }
    }

    public List<ConversationPri> getConversationsCommeB() {
        return conversationsCommeB;
    }

    public void addConversationCommeB(ConversationPri conversation) {
        if (conversation != null && !conversationsCommeB.contains(conversation)) {
            conversationsCommeB.add(conversation);
            conversation.setDestinataireCP(this);
        }
    }

    public void removeConversationCommeB(ConversationPri conversation) {
        if (conversation != null) {
            conversationsCommeB.remove(conversation);
            conversation.setDestinataireCP(null);
        }
    }

    public List<MembreGroupe> getGroupes() {
        return groupes;
    }

    public void addGroupe(MembreGroupe membreGroupe) {
        if (membreGroupe != null && !groupes.contains(membreGroupe)) {
            groupes.add(membreGroupe);
            membreGroupe.setMembre(this);
        }
    }

    public void removeGroupe(MembreGroupe membreGroupe) {
        if (membreGroupe != null) {
            groupes.remove(membreGroupe);
            membreGroupe.setMembre(null);
        }
    }

    public List<DemandeAmi> getDemandeAmiExp() {
        return demandeAmiExp;
    }

    public void addDemandeAmiExp(DemandeAmi demande) {
        if (demande != null && !demandeAmiExp.contains(demande)) {
            demandeAmiExp.add(demande);
            demande.setExpediteurAmi(this);
        }
    }

    public void removeDemandeAmiExp(DemandeAmi demande) {
        if (demande != null) {
            demandeAmiExp.remove(demande);
            demande.setExpediteurAmi(null);
        }
    }

    public List<DemandeAmi> getDemandeAmiDes() {
        return demandeAmiDes;
    }

    public void addDemandeAmiDes(DemandeAmi demande) {
        if (demande != null && !demandeAmiDes.contains(demande)) {
            demandeAmiDes.add(demande);
            demande.setDestinataireAmi(this);
        }
    }

    public void removeDemandeAmiDes(DemandeAmi demande) {
        if (demande != null) {
            demandeAmiDes.remove(demande);
            demande.setDestinataireAmi(null);
        }
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message message) {
        if (message != null && !messages.contains(message)) {
            messages.add(message);
            message.setExpedi(this);
        }
    }

    public void removeMessage(Message message) {
        if (message != null) {
            messages.remove(message);
            message.setExpedi(null);
        }
    }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", dtInscription=" + dtInscription +
                '}';
    }

    public String getMotDePasse() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMotDePasse'");
    }

}

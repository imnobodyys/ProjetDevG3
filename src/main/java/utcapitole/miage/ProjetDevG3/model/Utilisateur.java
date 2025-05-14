package utcapitole.miage.projetDevG3.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


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
    private List<Commentaire> commentaires = new ArrayList<>();

    @OneToMany(mappedBy = "expediteurCP")
    private List<ConversationPri> conversationsCommeA = new ArrayList<>();

    @OneToMany(mappedBy = "destinataireCP")
    private List<ConversationPri> conversationsCommeB = new ArrayList<>();

    @OneToMany(mappedBy = "membre", cascade = CascadeType.ALL)
    private List<MembreGroupe> groupes = new ArrayList<>();

    @OneToMany(mappedBy = "expediteurAmi")
    private List<DemandeAmi> demandeAmiExp = new ArrayList<>();

    @OneToMany(mappedBy = "destinataireAmi")
    private List<DemandeAmi> demandeAmiDes = new ArrayList<>();

    @OneToMany(mappedBy = "expedi", cascade = CascadeType.ALL)
    private List<Message> messages = new ArrayList<>();




    //getters et setters
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
    
}

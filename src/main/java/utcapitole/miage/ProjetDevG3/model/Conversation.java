package utcapitole.miage.projetdevg3.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;

/**
 * Classe Conversation
 * Représente une conversation entre utilisateurs
 * Chaque conversation peut contenir plusieurs messages
 * et chaque message appartient à une conversation
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Conversation {

    /**
     * Attributs
     * id : identifiant de la conversation
     * messages : liste des messages de la conversation
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * Relation
     * messages : liste des messages de la conversation
     */
    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL)
    private List<Message> messages;

    /**
     * getter and setter
     * id : identifiant de la conversation
     */
    public long getId() {
        return id;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message message) {
        if (message == null)
            return;
        if (!messages.contains(message)) {
            messages.add(message);
            message.setConversation(this);
        }
    }

    public void removeMessage(Message message) {
        if (message == null)
            return;
        if (messages.remove(message)) {
            message.setConversation(null);
        }
    }

    public void setId(Long id) {
        this.id = id;
    }

}
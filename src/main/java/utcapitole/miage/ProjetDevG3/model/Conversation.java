package utcapitole.miage.projetDevG3.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Conversation {
    @Id
    @GeneratedValue
    private long id;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL)
    private List<Message> messages;


    //getters et setters
    public long getId() {
        return id;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message message) {
        if (message == null) return;
        if (!messages.contains(message)) {
            messages.add(message);
            message.setConversation(this);
        }
    }

    public void removeMessage(Message message) {
        if (message == null) return;
        if (messages.remove(message)) {
            message.setConversation(null);
        }
    }
    
}

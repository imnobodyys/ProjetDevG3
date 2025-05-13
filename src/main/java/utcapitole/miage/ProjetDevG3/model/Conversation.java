package utcapitole.miage.projetDevG3.model;

import java.util.List;

import org.apache.logging.log4j.message.Message;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Conversation {
    @Id
    @GeneratedValue
    private long id;

    @OneToMany(mappedBy = "Conversation", cascade = CascadeType.ALL)
    private List<Message> messages;
}

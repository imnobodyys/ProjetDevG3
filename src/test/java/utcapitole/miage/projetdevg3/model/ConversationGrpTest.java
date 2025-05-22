package utcapitole.miage.projetdevg3.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConversationGrpTest {
    private ConversationGrp conversationGrp;
    private Groupe groupe;

    @BeforeEach
    public void setup() {
        conversationGrp = new ConversationGrp();
        groupe = new Groupe(); // Assure-toi que Groupe a un constructeur par défaut
    }

    @Test
    void testSetAndGetGroupeCon() {
        conversationGrp.setGroupeCon(groupe);
        assertEquals(groupe, conversationGrp.getGroupeCon());
    }

    @Test
    void testInheritanceId() {
        conversationGrp.setId(42L);
        assertEquals(42L, conversationGrp.getId());
    }

}

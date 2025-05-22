package utcapitole.miage.projetdevg3.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConversationPriTest {

    private ConversationPri conversationPri;
    private Utilisateur expediteur;
    private Utilisateur destinataire;

    @BeforeEach
    void setup() {
        conversationPri = new ConversationPri();
        expediteur = new Utilisateur(); // Assure-toi que Utilisateur a un constructeur par défaut
        destinataire = new Utilisateur();
    }

    @Test
    void testSetAndGetExpediteurCP() {
        conversationPri.setExpediteurCP(expediteur);
        assertEquals(expediteur, conversationPri.getExpediteurCP());
    }

    @Test
    void testSetAndGetDestinataireCP() {
        conversationPri.setDestinataireCP(destinataire);
        assertEquals(destinataire, conversationPri.getDestinataireCP());
    }

    @Test
    void testInheritanceId() {
        conversationPri.setId(100L);
        assertEquals(100L, conversationPri.getId());
    }
}

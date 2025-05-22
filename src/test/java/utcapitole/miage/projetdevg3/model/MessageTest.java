package utcapitole.miage.projetdevg3.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class MessageTest {

    @Test
    public void testGettersAndSetters() {
        Message message = new Message();
        message.setId(10L);
        message.setContenu("Bonjour !");
        
        Utilisateur utilisateur = new Utilisateur();
        message.setExpedi(utilisateur);
        
        Conversation conversation = new Conversation();
        message.setConversation(conversation);

        LocalDateTime now = LocalDateTime.now();
        message.setDtEnvoi(now);

        assertEquals(10L, message.getId());
        assertEquals("Bonjour !", message.getContenu());
        assertEquals(utilisateur, message.getExpedi());
        assertEquals(conversation, message.getConversation());
        assertEquals(now, message.getDtEnvoi());
    }

    @Test
    public void testSetDateEnvoiPrePersist() {
        Message message = new Message();
        message.setDateEnvoi();
        assertNotNull(message.getDtEnvoi());
        // Optionnel : vérifier que la date est proche de maintenant
        assertTrue(message.getDtEnvoi().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
}

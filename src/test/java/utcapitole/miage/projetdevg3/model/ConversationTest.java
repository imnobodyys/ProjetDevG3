package utcapitole.miage.projetdevg3.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConversationTest {

    private Conversation conversation;
    private Message message1;
    private Message message2;

    @BeforeEach
    void setup() {
        conversation = new Conversation();
        // On initialise la liste messages manuellement pour éviter NullPointerException
        conversation.setMessages(new ArrayList<>());

        message1 = new Message();
        message2 = new Message();
    }

    @Test
    void testGetAndSetId() {
        conversation.setId(123L);
        assertEquals(123L, conversation.getId());
    }

    @Test
    void testAddMessage() {
        conversation.addMessage(message1);
        assertTrue(conversation.getMessages().contains(message1));
        assertEquals(conversation, message1.getConversation());
    }

    @Test
    public void testAddNullMessageDoesNothing() {
        conversation.addMessage(null);
        assertTrue(conversation.getMessages().isEmpty());
    }

    @Test
    void testAddDuplicateMessageDoesNothing() {
        conversation.addMessage(message1);
        int sizeBefore = conversation.getMessages().size();
        conversation.addMessage(message1);
        int sizeAfter = conversation.getMessages().size();
        assertEquals(sizeBefore, sizeAfter);
    }

    @Test
    void testRemoveMessage() {
        conversation.addMessage(message1);
        conversation.removeMessage(message1);
        assertFalse(conversation.getMessages().contains(message1));
        assertNull(message1.getConversation());
    }

    @Test
    void testRemoveNullMessageDoesNothing() {
        conversation.removeMessage(null); // ne doit rien faire
    }

    @Test
    void testRemoveMessageNotPresentDoesNothing() {
        conversation.addMessage(message1);
        conversation.removeMessage(message2); // message2 n'est pas dans la liste
        assertTrue(conversation.getMessages().contains(message1));
    }

}


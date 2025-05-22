package utcapitole.miage.projetdevg3.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GroupeTest {

     private Groupe groupe;
    private MembreGroupe membre1;
    private MembreGroupe membre2;
    private ConversationGrp conversationGrp;

    @BeforeEach
    public void setUp() {
        groupe = new Groupe();
        membre1 = new MembreGroupe();
        membre2 = new MembreGroupe();
        conversationGrp = new ConversationGrp();

        // Initialiser la liste membres pour éviter NullPointerException
        groupe.setMembres(new ArrayList<>());
    }

    @Test
    public void testGettersAndSetters() {
        groupe.setId(10L);
        groupe.setNom("Nom Test");
        groupe.setDescription("Description Test");
        LocalDateTime now = LocalDateTime.now();
        groupe.setDtCreation(now);
        groupe.setType(Groupe.TypeGroupe.PRIVE);
        groupe.setActif(false);

        assertEquals(10L, groupe.getId());
        assertEquals("Nom Test", groupe.getNom());
        assertEquals("Description Test", groupe.getDescription());
        assertEquals(now, groupe.getDtCreation());
        assertEquals(Groupe.TypeGroupe.PRIVE, groupe.getType());
        assertFalse(groupe.isActif());
    }

    @Test
    public void testAddMembre() {
        groupe.addMembre(membre1);
        assertTrue(groupe.getMembres().contains(membre1));
        assertEquals(groupe, membre1.getGroupe());
    }

    @Test
    public void testAddMembre_NullDoesNothing() {
        groupe.addMembre(null);
        assertTrue(groupe.getMembres().isEmpty());
    }

    @Test
    public void testAddMembre_DuplicateDoesNothing() {
        groupe.addMembre(membre1);
        int sizeBefore = groupe.getMembres().size();
        groupe.addMembre(membre1);
        int sizeAfter = groupe.getMembres().size();
        assertEquals(sizeBefore, sizeAfter);
    }

    @Test
    public void testRemoveMembre() {
        groupe.addMembre(membre1);
        groupe.removeMembre(membre1);
        assertFalse(groupe.getMembres().contains(membre1));
        assertNull(membre1.getGroupe());
    }

    @Test
    public void testRemoveMembre_NullDoesNothing() {
        groupe.removeMembre(null); // ne doit rien planter
    }

    @Test
    public void testSetConversationGrp_SetsBidirectionalRelation() {
        groupe.setConversationGrp(conversationGrp);
        assertEquals(conversationGrp, groupe.getConversationGrp());
        assertEquals(groupe, conversationGrp.getGroupeCon());
    }

    @Test
    public void testSetConversationGrp_NullRemovesBidirectionalRelation() {
        groupe.setConversationGrp(conversationGrp);
        groupe.setConversationGrp(null);
        assertNull(groupe.getConversationGrp());
        assertNull(conversationGrp.getGroupeCon());
    }

   
}

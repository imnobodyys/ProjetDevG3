package utcapitole.miage.projetdevg3.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EvenementTest {

     private Evenement evenement;
    private Utilisateur auteur;
    private Utilisateur participant1;
    private Utilisateur participant2;
    private LocalDateTime now;

    @BeforeEach
    public void setup() {
        evenement = new Evenement();
        auteur = new Utilisateur();
        participant1 = new Utilisateur();
        participant2 = new Utilisateur();
        now = LocalDateTime.now();
    }

    @Test
    public void testGettersAndSetters() {
        evenement.setTitre("Titre Test");
        evenement.setDescription("Description Test");
        evenement.setContenu("Contenu Test");
        evenement.setDatePublication(now);
        evenement.setVisibilite(VisibiliteEvenement.PUBLIC);
        evenement.setAuteur(auteur);

        assertEquals("Titre Test", evenement.getTitre());
        assertEquals("Description Test", evenement.getDescription());
        assertEquals("Contenu Test", evenement.getContenu());
        assertEquals(now, evenement.getDatePublication());
        assertEquals(VisibiliteEvenement.PUBLIC, evenement.getVisibilite());
        assertEquals(auteur, evenement.getAuteur());
    }

    @Test
    public void testAddParticipant() {
        evenement.addParticipant(participant1);
        assertTrue(evenement.getParticipants().contains(participant1));
        // On suppose que Utilisateur a bien la méthode addEvenement, vérifiée par contrat
        // (à vérifier dans la classe Utilisateur)
    }

    @Test
    public void testAddNullParticipantDoesNothing() {
        evenement.addParticipant(null);
        assertTrue(evenement.getParticipants().isEmpty());
    }

    @Test
    public void testAddDuplicateParticipantDoesNothing() {
        evenement.addParticipant(participant1);
        int sizeBefore = evenement.getParticipants().size();
        evenement.addParticipant(participant1);
        int sizeAfter = evenement.getParticipants().size();
        assertEquals(sizeBefore, sizeAfter);
    }

    @Test
    public void testRemoveNullParticipantDoesNothing() {
        evenement.removeParticipant(null); // Ne doit rien faire sans erreur
    }

    
}

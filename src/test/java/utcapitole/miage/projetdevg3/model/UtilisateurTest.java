package utcapitole.miage.projetdevg3.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UtilisateurTest {
    private Utilisateur utilisateur;

    @BeforeEach
    void setup() {
        utilisateur = new Utilisateur("Dupont", "Jean", "jean.dupont@example.com", "secret123");
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals("Dupont", utilisateur.getNom());
        assertEquals("Jean", utilisateur.getPrenom());
        assertEquals("jean.dupont@example.com", utilisateur.getEmail());
        assertNotNull(utilisateur.getDtInscription());
        assertTrue(utilisateur.getPosts().isEmpty());
    }

    @Test
    void testSetters() {
        utilisateur.setNom("Durand");
        utilisateur.setPrenom("Marie");
        utilisateur.setEmail("marie.durand@example.com");
        utilisateur.setMdp("newpass");

        assertEquals("Durand", utilisateur.getNom());
        assertEquals("Marie", utilisateur.getPrenom());
        assertEquals("marie.durand@example.com", utilisateur.getEmail());
        assertEquals("newpass", utilisateur.getMdp());
    }

    @Test
    void testAddAndRemovePost() {
        Post post = new Post();
        utilisateur.addPost(post);

        assertTrue(utilisateur.getPosts().contains(post));
        assertEquals(utilisateur, post.getAuteur());

        utilisateur.removePost(post);
        assertFalse(utilisateur.getPosts().contains(post));
        assertNull(post.getAuteur());
    }

}

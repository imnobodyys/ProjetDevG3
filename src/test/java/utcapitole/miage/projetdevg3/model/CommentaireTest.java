package utcapitole.miage.projetdevg3.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CommentaireTest {
     private Commentaire commentaire;
    private Utilisateur utilisateur;
    private Post post;
    private LocalDateTime now;

    @BeforeEach
    void setup() {
        commentaire = new Commentaire();
        utilisateur = new Utilisateur(); // Assure-toi que cette classe a un constructeur par défaut
        post = new Post();               // idem
        now = LocalDateTime.now();
    }

    @Test
    void testSetAndGetContenu() {
        String contenu = "Ceci est un commentaire";
        commentaire.setContenu(contenu);
        assertEquals(contenu, commentaire.getContenu());
    }

    @Test
    void testSetAndGetDateEnvoi() {
        commentaire.setDateEnvoi(now);
        assertEquals(now, commentaire.getDateEnvoi());
    }

    @Test
    void testSetAndGetExpediteur() {
        commentaire.setExpediteur(utilisateur);
        assertEquals(utilisateur, commentaire.getExpediteur());
    }

    @Test
    void testSetAndGetPost() {
        commentaire.setPost(post);
        assertEquals(post, commentaire.getPost());
    }

    @Test
    void testIdIsNullByDefault() {
        assertNull(commentaire.getId());
    }

}


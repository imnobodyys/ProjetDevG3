package utcapitole.miage.projetdevg3.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PostTest {
     private Post post;
    private Post repost;
    private Commentaire commentaire;

    @BeforeEach
    void setUp() {
        post = new Post();
        post.setContenu("Contenu original");
        post.setVisibilite(VisibilitePost.PUBLIC);
        post.setAuteur(new Utilisateur());

        repost = new Post();
        repost.setContenu("Repost contenu");

        commentaire = new Commentaire();
        commentaire.setContenu("Ceci est un commentaire");
    }

    @Test
    void testAddAndRemoveRepost() {
        assertTrue(post.getReposts().isEmpty());

        post.addRepost(repost);
        assertEquals(1, post.getReposts().size());
        assertTrue(post.getReposts().contains(repost));
        assertEquals(post, repost.getOriginalPost());

        post.removeRepost(repost);
        assertTrue(post.getReposts().isEmpty());
        assertNull(repost.getOriginalPost());
    }

    @Test
    void testAddAndRemoveCommentaire() {
        assertTrue(post.getCommentaires().isEmpty());

        post.addCommentaire(commentaire);
        assertEquals(1, post.getCommentaires().size());
        assertTrue(post.getCommentaires().contains(commentaire));
        assertEquals(post, commentaire.getPost());

        post.removeCommentaire(commentaire);
        assertTrue(post.getCommentaires().isEmpty());
        assertNull(commentaire.getPost());
    }

    @Test
    void testGettersSetters() {
        assertEquals("Contenu original", post.getContenu());
        assertEquals(VisibilitePost.PUBLIC, post.getVisibilite());
        assertNotNull(post.getAuteur());
        
        LocalDateTime now = LocalDateTime.now();
        post.setCreatedAt(now);
        assertEquals(now, post.getCreatedAt());
    }

}

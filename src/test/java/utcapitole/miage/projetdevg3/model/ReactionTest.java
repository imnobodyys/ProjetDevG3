package utcapitole.miage.projetdevg3.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ReactionTest {

     private Reaction reaction;
    private Utilisateur utilisateur;
    private Post post;

    @BeforeEach
    void setUp() {
        reaction = new Reaction();
        utilisateur = new Utilisateur();
        post = new Post();
    }

    @Test
    void testGettersAndSetters() {
        reaction.setId(1L); // Si tu as un setter pour l'id, sinon ignore
        LocalDateTime now = LocalDateTime.now();
        reaction.setDtEnvoi(now);
        reaction.setType(TypeReaction.JAIME);
        reaction.setExpedient(utilisateur);
        reaction.setPost(post);

        assertEquals(1L, reaction.getId());
        assertEquals(now, reaction.getDtEnvoi());
        assertEquals(TypeReaction.JAIME, reaction.getType());
        assertEquals(utilisateur, reaction.getExpedient());
        assertEquals(post, reaction.getPost());
    }
}

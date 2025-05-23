package utcapitole.miage.projetdevg3.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import utcapitole.miage.projetdevg3.model.Post;
import utcapitole.miage.projetdevg3.model.Reaction;
import utcapitole.miage.projetdevg3.model.TypeReaction;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.repository.ReactionRepository;

class ReactionServiceTest {

    @Mock
    private ReactionRepository reactionRepository;

    @InjectMocks
    private ReactionService reactionService;

    private Utilisateur utilisateur;
    private Post post;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        utilisateur = new Utilisateur();
        utilisateur.setId(1L);

        post = new Post();
        post.setId(1L);
    }

    /**
     * Teste l'ajout d'une nouvelle réaction si aucune n'existe.
     */
    @Test
    void testAjouterNouvelleReaction() {
        when(reactionRepository.findByExpedientAndPost(utilisateur, post)).thenReturn(Optional.empty());

        reactionService.ajouterOuModifierReaction(utilisateur, post, TypeReaction.JAIME);

        verify(reactionRepository).save(argThat(r -> r.getPost().equals(post) &&
                r.getExpedient().equals(utilisateur) &&
                r.getType() == TypeReaction.JAIME &&
                r.getDtEnvoi() != null));
    }

    /**
     * Teste la mise à jour d'une réaction existante.
     */
    @Test
    void testModifierReactionExistante() {
        Reaction existante = new Reaction();
        existante.setPost(post);
        existante.setExpedient(utilisateur);
        existante.setType(TypeReaction.COOL);

        when(reactionRepository.findByExpedientAndPost(utilisateur, post)).thenReturn(Optional.of(existante));

        reactionService.ajouterOuModifierReaction(utilisateur, post, TypeReaction.BRAVO);

        assertEquals(TypeReaction.BRAVO, existante.getType());
        verify(reactionRepository).save(existante);
    }

    /**
     * Teste que compterReactions retourne bien le total des types de réactions.
     */
    @Test
    void testCompterReactions() {
        Reaction r1 = new Reaction();
        r1.setType(TypeReaction.JAIME);
        Reaction r2 = new Reaction();
        r2.setType(TypeReaction.JAIME);
        Reaction r3 = new Reaction();
        r3.setType(TypeReaction.TRISTE);

        when(reactionRepository.findByPost(post)).thenReturn(List.of(r1, r2, r3));

        Map<TypeReaction, Long> result = reactionService.compterReactions(post);

        assertEquals(2L, result.get(TypeReaction.JAIME));
        assertEquals(1L, result.get(TypeReaction.TRISTE));
    }

    /**
     * Teste que compterReactions retourne une map vide si le post est null.
     */
    @Test
    void testCompterReactionsAvecPostNull() {
        Map<TypeReaction, Long> result = reactionService.compterReactions(null);
        assertTrue(result.isEmpty());
    }
}

package utcapitole.miage.projetdevg3.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import utcapitole.miage.projetdevg3.model.Groupe;
import utcapitole.miage.projetdevg3.model.Post;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.repository.MembreGroupeRepository;
import utcapitole.miage.projetdevg3.repository.PostRepository;

class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private MembreGroupeRepository membreGroupeRepository;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void publierDansGroupe_enregistrePostEtDefinitCreatedAt() {
        Utilisateur auteur = new Utilisateur();
        auteur.setId(1L);

        Groupe groupe = new Groupe();
        groupe.setId(1L);

        Post post = new Post();
        post.setAuteur(auteur);
        post.setGroupe(groupe);
        post.setContenu("Test");

        when(membreGroupeRepository.existsByGroupeIdAndMembreId(1L, 1L)).thenReturn(true);
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Post resultat = postService.publierDansGroupe(post);

        assertNotNull(resultat);
        assertEquals("Test", resultat.getContenu());
        verify(postRepository).save(post);
    }

    @Test
    void publierDansGroupe_utilisateurNonMembre_IllegalArgumentException() {
        Utilisateur auteur = new Utilisateur();
        auteur.setId(1L);

        Groupe groupe = new Groupe();
        groupe.setId(1L);

        Post post = new Post();
        post.setAuteur(auteur);
        post.setGroupe(groupe);

        when(membreGroupeRepository.existsByGroupeIdAndMembreId(1L, 1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            postService.publierDansGroupe(post);
        });
    }
}

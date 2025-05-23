package utcapitole.miage.projetdevg3.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import utcapitole.miage.projetdevg3.config.SecurityConfig;
import utcapitole.miage.projetdevg3.model.Commentaire;
import utcapitole.miage.projetdevg3.model.Post;
import utcapitole.miage.projetdevg3.model.TypeReaction;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.service.CommentaireService;
import utcapitole.miage.projetdevg3.service.PostService;
import utcapitole.miage.projetdevg3.service.ReactionService;
import utcapitole.miage.projetdevg3.service.UtilisateurService;

/**
 * Classe de test pour le contrôleur PostController.
 * Vérifie les opérations liées aux publications : création, affichage,
 * suppression,
 * commentaires et réactions.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = true)
@WithMockUser(username = "jean@example.com")
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private utcapitole.miage.projetdevg3.repository.UtilisateurRepository utilisateurRepository;

    @MockBean
    private PostService postService;

    @MockBean
    private UtilisateurService utilisateurService;

    @MockBean
    private CommentaireService commentaireService;

    @MockBean
    private ReactionService reactionService;

    private Utilisateur utilisateur;
    private Post post;

    @BeforeEach
    void setUp() {
        utilisateur = new Utilisateur();
        utilisateur.setId(1L);
        utilisateur.setNom("Jean");
        utilisateur.setEmail("jean@example.com");

        post = new Post();
        post.setId(1L);
        post.setAuteur(utilisateur);
        post.setContenu("Test Post");
    }

    /**
     * Teste l'affichage du formulaire de création de post.
     */
    @Test
    void testAfficherFormulaire() throws Exception {
        mockMvc.perform(get("/posts/nouveau"))
                .andExpect(status().isOk())
                .andExpect(view().name("form-post"))
                .andExpect(model().attributeExists("post"));
    }

    /**
     * Teste la publication d'un post.
     */
    @Test
    void testPublierPost() throws Exception {
        when(utilisateurService.findByEmail(anyString())).thenReturn(utilisateur);

        mockMvc.perform(post("/posts/publier")
                .with(csrf())
                .param("contenu", "Un nouveau post"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/liste"));

        verify(postService).creerPost(any(Post.class), eq(utilisateur));
    }

    /**
     * Teste l'affichage de la liste des posts publics.
     */
    @Test
    void testAfficherPosts() throws Exception {
        when(postService.getPostsPublics()).thenReturn(List.of(post));
        when(reactionService.compterReactions(any(Post.class))).thenReturn(Map.of());
        when(utilisateurService.findByEmail(anyString())).thenReturn(utilisateur);

        mockMvc.perform(get("/posts/liste"))
                .andExpect(status().isOk())
                .andExpect(view().name("list-post"))
                .andExpect(model().attributeExists("posts", "reactionsStats", "pageType", "utilisateurConnecte"));
    }

    /**
     * Teste l'affichage de la liste des posts de l'utilisateur connecté.
     */
    @Test
    void testAfficherMesPosts() throws Exception {
        when(utilisateurService.findByEmail(anyString())).thenReturn(utilisateur);
        when(postService.getPostsParAuteur(utilisateur)).thenReturn(List.of(post));
        when(reactionService.compterReactions(post)).thenReturn(Map.of());

        mockMvc.perform(get("/posts/mes-posts"))
                .andExpect(status().isOk())
                .andExpect(view().name("list-post"))
                .andExpect(model().attributeExists("posts", "reactionsStats", "pageType", "utilisateurConnecte"));
    }

    /**
     * Teste la suppression d'un post.
     */
    @Test
    void testSupprimerPost() throws Exception {
        when(utilisateurService.findByEmail(anyString())).thenReturn(utilisateur);
        when(postService.supprimerPost(1L, utilisateur)).thenReturn(true);

        mockMvc.perform(post("/posts/supprimer/1")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/liste"));
    }

    /**
     * Teste la consultation d'un post et de ses commentaires.
     */
    @Test
    void testVoirPost() throws Exception {
        when(postService.getPostById(1L)).thenReturn(post);
        when(commentaireService.getCommentaires(post)).thenReturn(List.of());

        mockMvc.perform(get("/posts/voir/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("voir-post"))
                .andExpect(model().attributeExists("post", "commentaires", "nouveauCommentaire"));
    }

    /**
     * Teste l'ajout d'un commentaire à un post.
     */
    @Test
    void testCommenterPost() throws Exception {
        when(utilisateurService.findByEmail(anyString())).thenReturn(utilisateur);

        mockMvc.perform(post("/posts/1/commenter")
                .with(csrf())
                .param("contenu", "Un commentaire"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/liste"));

        verify(postService).ajouterCommentaire(1L, "Un commentaire", utilisateur);
    }

    /**
     * Teste l'ajout ou la modification d'une réaction à un post.
     */
    @Test
    void testReagirAuPost() throws Exception {
        when(utilisateurService.findByEmail(anyString())).thenReturn(utilisateur);
        when(postService.getPostById(1L)).thenReturn(post);

        mockMvc.perform(post("/posts/1/reaction")
                .with(csrf())
                .param("type", "JAIME"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/liste"));

        verify(reactionService).ajouterOuModifierReaction(utilisateur, post, TypeReaction.JAIME);
    }
}

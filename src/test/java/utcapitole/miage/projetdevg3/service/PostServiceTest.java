package utcapitole.miage.projetdevg3.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import utcapitole.miage.projetdevg3.model.Groupe;
import utcapitole.miage.projetdevg3.model.Post;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.model.VisibilitePost;
import utcapitole.miage.projetdevg3.repository.CommentaireRepository;
import utcapitole.miage.projetdevg3.repository.MembreGroupeRepository;
import utcapitole.miage.projetdevg3.repository.PostRepository;
import utcapitole.miage.projetdevg3.service.PostService;

class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private MembreGroupeRepository membreGroupeRepository;

    @InjectMocks
    private PostService postService;

    @Mock
    private CommentaireRepository commentaireRepository;

    private Utilisateur utilisateur;
    private Post post;

    /**
     * Teste que getPostById retourne bien le post si trouvé.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        utilisateur = new Utilisateur();
        utilisateur.setId(1L);
        utilisateur.setNom("Jean");

        post = new Post();
        post.setId(1L);
        post.setAuteur(utilisateur);
    }

    /**
     * Teste que getPostById lance une exception si le post est introuvable.
     */
    @Test
    void testGetPostById_NotFound() {
        when(postRepository.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            postService.getPostById(2L);
        });

        assertEquals("Post non trouvé", exception.getMessage());
    }

    /**
     * Teste la création d'un post avec auteur et date automatique.
     */
    @Test
    void testCreerPost() {
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Post nouveauPost = new Post();
        Post result = postService.creerPost(nouveauPost, utilisateur);

        assertEquals(utilisateur, result.getAuteur());
        assertNotNull(result.getCreatedAt());
        verify(postRepository).save(nouveauPost);
    }

    /**
     * Teste la récupération des posts publics.
     */
    @Test
    void testGetPostsPublics() {
        List<Post> liste = List.of(new Post());
        when(postRepository.findByVisibilite(VisibilitePost.PUBLIC)).thenReturn(liste);

        List<Post> result = postService.getPostsPublics();
        assertEquals(1, result.size());
    }

    /**
     * Teste la récupération des posts d'un auteur donné.
     */
    @Test
    void testGetPostsParAuteur() {
        List<Post> liste = List.of(post);
        when(postRepository.findByAuteur(utilisateur)).thenReturn(liste);

        List<Post> result = postService.getPostsParAuteur(utilisateur);
        assertEquals(1, result.size());
    }

    /**
     * Teste l'ajout d'un commentaire à un post existant.
     */
    @Test
    void testAjouterCommentaire() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        postService.ajouterCommentaire(1L, "Contenu du commentaire", utilisateur);

        verify(commentaireRepository).save(argThat(c -> c.getContenu().equals("Contenu du commentaire") &&
                c.getExpediteur().equals(utilisateur) &&
                c.getPost().equals(post)));
    }

    /**
     * Teste la suppression d'un post par son auteur.
     */
    @Test
    void testSupprimerPost_Autorise() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        boolean result = postService.supprimerPost(1L, utilisateur);
        assertTrue(result);
        verify(postRepository).delete(post);
    }

    /**
     * Teste le refus de suppression si l'utilisateur n'est pas l'auteur du post.
     */
    @Test
    void testSupprimerPost_NonAutorise() {
        Utilisateur autre = new Utilisateur();
        autre.setId(2L);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        boolean result = postService.supprimerPost(1L, autre);
        assertFalse(result);
        verify(postRepository, never()).delete(post);
    }

    /**
     * Teste la suppression d'un post introuvable.
     */
    @Test
    void testSupprimerPost_NotFound() {
        when(postRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> postService.supprimerPost(99L, utilisateur));
    }

}

package utcapitole.miage.projetdevg3.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import utcapitole.miage.projetdevg3.model.Commentaire;
import utcapitole.miage.projetdevg3.model.Post;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.repository.CommentaireRepository;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitaires pour le service CommentaireService.
 */
class CommentaireServiceTest {

    @Mock
    private CommentaireRepository commentaireRepository;

    @InjectMocks
    private CommentaireService commentaireService;

    private Utilisateur utilisateur;
    private Post post;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        utilisateur = new Utilisateur();
        utilisateur.setId(1L);
        utilisateur.setNom("Jean");

        post = new Post();
        post.setId(1L);
    }

    /**
     * Teste que le service ajoute correctement un commentaire avec les bons champs.
     */
    @Test
    void testAjouterCommentaire() {
        String contenu = "Très bon post !";

        commentaireService.ajouterCommentaire(utilisateur, post, contenu);

        verify(commentaireRepository)
                .save(org.mockito.ArgumentMatchers.argThat(commentaire -> commentaire.getContenu().equals(contenu)
                        && commentaire.getPost().equals(post)
                        && commentaire.getExpediteur().equals(utilisateur)
                        && commentaire.getDateEnvoi() != null));
    }

    /**
     * Teste la récupération des commentaires associés à un post.
     */
    @Test
    void testGetCommentaires() {
        Commentaire commentaire = new Commentaire();
        commentaire.setPost(post);
        commentaire.setContenu("Commentaire de test");

        when(commentaireRepository.findByPostOrderByDtEnvoiAsc(post)).thenReturn(List.of(commentaire));

        List<Commentaire> result = commentaireService.getCommentaires(post);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContenu()).isEqualTo("Commentaire de test");
    }
}
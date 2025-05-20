package utcapitole.miage.projetDevG3.Service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.transaction.Transactional;
import utcapitole.miage.projetDevG3.Repository.GroupeRepository;
import utcapitole.miage.projetDevG3.Repository.MembreGroupeRepository;
import utcapitole.miage.projetDevG3.Repository.PostRepository;
import utcapitole.miage.projetDevG3.Repository.UtilisateurRepository;
import utcapitole.miage.projetDevG3.model.Groupe;
import utcapitole.miage.projetDevG3.model.MembreGroupe;
import utcapitole.miage.projetDevG3.model.Post;
import utcapitole.miage.projetDevG3.model.StatutMembre;
import utcapitole.miage.projetDevG3.model.Utilisateur;
import utcapitole.miage.projetDevG3.model.VisibilitePost;

@SpringBootTest
@Transactional
public class PostServiceTest {
     @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MembreGroupeRepository membreGroupeRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private GroupeRepository groupeRepository;

    private Utilisateur utilisateur;
    private Groupe groupe;

    @BeforeEach
    void setUp() {
        // Création utilisateur
        utilisateur = new Utilisateur();
        utilisateur.setNom("TestUser");
        utilisateur.setEmail("test@example.com");
        utilisateur = utilisateurRepository.save(utilisateur);

        // Création groupe
        groupe = new Groupe();
        groupe.setNom("GroupeTest");
        groupe = groupeRepository.save(groupe);

        // Ajout utilisateur comme membre accepté dans le groupe
        MembreGroupe membreGroupe = new MembreGroupe(utilisateur, groupe);
        membreGroupe.setStatut(StatutMembre.ACCEPTE);  // statut explicite
        membreGroupeRepository.save(membreGroupe);
    }

    @Test
    void publierDansGroupe_enregistrePostEtDefinitCreatedAt() {
        Post post = new Post();
        post.setAuteur(utilisateur);
        post.setGroupe(groupe);
        post.setContenu("Contenu test");
        post.setVisibilite(VisibilitePost.PUBLIC);

        Post postSauvegarde = postService.publierDansGroupe(post);

        assertThat(postSauvegarde.getId()).isNotNull();
        assertThat(postSauvegarde.getCreatedAt()).isNotNull();
        assertThat(postSauvegarde.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void publierDansGroupe_utilisateurNonMembre_IllegalArgumentException() {
        Utilisateur nonMembre = new Utilisateur();
        nonMembre.setNom("NonMembre");
        nonMembre.setEmail("nonmembre@example.com");
        nonMembre = utilisateurRepository.save(nonMembre);

        Post post = new Post();
        post.setAuteur(nonMembre);
        post.setGroupe(groupe);
        post.setContenu("Contenu test");
        post.setVisibilite(VisibilitePost.PUBLIC);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            postService.publierDansGroupe(post);
        });

        assertThat(exception.getMessage()).contains("n'est pas membre du groupe");
    }

}

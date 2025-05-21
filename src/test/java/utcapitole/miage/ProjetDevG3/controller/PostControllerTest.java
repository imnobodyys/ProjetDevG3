package utcapitole.miage.projetDevG3.controller;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import utcapitole.miage.projetdevg3.model.Post;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.service.PostService;
import utcapitole.miage.projetdevg3.service.UtilisateurService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.security.Principal;
import java.util.List;

@AutoConfigureMockMvc
@SpringBootTest
@WithMockUser(username = "test@example.com")  // même nom que dans les mocks
class PostControllerTest {
     @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @MockitoBean
    private UtilisateurService utilisateurService;

    @Test
    public void testVoirMesPostsPage() throws Exception {
        // Création de l'utilisateur mocké
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail("test@example.com");

        // Création des posts mockés
        Post post1 = new Post();
        post1.setId(1L);
        Post post2 = new Post();
        post2.setId(2L);
        List<Post> mesPosts = List.of(post1, post2);

        // Mock du service utilisateur
        when(utilisateurService.findByEmail("test@example.com")).thenReturn(utilisateur);

        // Mock du service post
        when(postService.getPostsByAuteur(utilisateur)).thenReturn(mesPosts);

        // Appel de la requête sans .principal() car Spring Security injecte le principal
        mockMvc.perform(get("/mes-posts"))
            .andExpect(status().isOk())
            .andExpect(view().name("posts/mes-posts"))
            .andExpect(model().attributeExists("mesPosts"))
            .andExpect(model().attribute("mesPosts", mesPosts));
    }

}

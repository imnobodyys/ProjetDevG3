package utcapitole.miage.projetdevg3.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import org.springframework.test.web.servlet.MockMvc;

import utcapitole.miage.projetdevg3.service.GroupeService;
import utcapitole.miage.projetdevg3.service.MembreGroupeService;
import utcapitole.miage.projetdevg3.service.UtilisateurService;
import utcapitole.miage.projetdevg3.controller.GroupeController;
import utcapitole.miage.projetdevg3.model.Groupe;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.repository.UtilisateurRepository;

@WebMvcTest(GroupeController.class)
@AutoConfigureMockMvc
public class GroupeControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GroupeService groupeService;

    @MockBean
    private MembreGroupeService membreGroupeService;
    @MockBean
    private UtilisateurService utilisateurService;

    @MockBean
    private UtilisateurRepository utilisateurRepository;

    // US17 - Création de groupe
    @Test
    @WithMockUser(username = "alice@example.com", roles = "USER")
    void testCreerGroupe() throws Exception {
        Utilisateur user = new Utilisateur("Alice", "Dupont", "alice@example.com", "password");

        when(utilisateurService.findByEmail("alice@example.com"))
                .thenReturn(user);

        mockMvc.perform(post("/groupes/creer")
                .param("nom", "Groupe Test")
                .param("description", "Description test")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/groupes/liste"));

        verify(groupeService).creerGroupe("Groupe Test", "Description test", user);
    }

    @Test
    public void testRejoindreGroupe_Unauthenticated() throws Exception {
        Long groupeId = 1L;

        mockMvc.perform(post("/groupes/rejoindre")
                .param("idGroupe", groupeId.toString())
                .with(csrf()))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(groupeService);
    }

}

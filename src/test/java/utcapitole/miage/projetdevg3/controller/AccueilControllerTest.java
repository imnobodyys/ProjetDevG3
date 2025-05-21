package utcapitole.miage.projetdevg3.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import utcapitole.miage.projetdevg3.service.UtilisateurService;
import utcapitole.miage.projetdevg3.model.Utilisateur;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AccueilController.class)
public class AccueilControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UtilisateurService utilisateurService;

    @WithMockUser(username = "alice@example.com")
    @Test
    void testAfficherAccueil_Authentifie() throws Exception {
        Utilisateur utilisateur = new Utilisateur("Alice", "Dupont", "alice@example.com", "1234");

        when(utilisateurService.getUtilisateurByEmail("alice@example.com")).thenReturn(utilisateur);

        mockMvc.perform(get("/accueil"))
                .andExpect(status().isOk())
                .andExpect(view().name("accueil"))
                .andExpect(model().attribute("isAuthenticated", true))
                .andExpect(model().attribute("utilisateur", utilisateur));
    }

}

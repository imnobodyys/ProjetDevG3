package utcapitole.miage.projetdevg3.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import utcapitole.miage.projetdevg3.service.UtilisateurService;
import utcapitole.miage.projetdevg3.controller.AccueilController;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.repository.UtilisateurRepository;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AccueilController.class)
public class AccueilControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UtilisateurService utilisateurService;

    @MockBean
    private UtilisateurRepository utilisateurRepository;
    

    /**
     * Test 1 - Affichage du tableau de bord
     * Utilisateur authentifié avec données valides
     */
    @Test
    @WithMockUser(username = "alice@example.com")
    void afficherAccueil_AvecUtilisateurValide_DoitAfficherDashboard() throws Exception {
        // Arrange
        Utilisateur mockUser = new Utilisateur("Alice", "Dupont", "alice@example.com", "1234");
        when(utilisateurService.getUtilisateurByEmail("alice@example.com")).thenReturn(mockUser);

        // Act & Assert
        mockMvc.perform(get("/accueil"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("utilisateur"))
                .andExpect(model().attribute("isAuthenticated", true));
    }
    

    /**
     * Teste le endpoint /test avec un utilisateur authentifié.
     * Vérifie que la réponse contient le nom d'utilisateur correct.
     */
    @WithMockUser(username = "charlie@example.com")
    @Test
    void testTestLogin_Authentifie() throws Exception {
        mockMvc.perform(get("/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Connecté en tant que : charlie@example.com"));
    }


}

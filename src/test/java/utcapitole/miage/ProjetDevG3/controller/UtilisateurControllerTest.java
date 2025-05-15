package utcapitole.miage.projetDevG3.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import utcapitole.miage.projetDevG3.Controller.UtilisateurController;
import utcapitole.miage.projetDevG3.Repository.UtilisateurRepository;
import utcapitole.miage.projetDevG3.Service.UtilisateurService;
import utcapitole.miage.projetDevG3.model.Utilisateur;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

@WebMvcTest(controllers = UtilisateurController.class)
public class UtilisateurControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UtilisateurService utilisateurService;

    @MockBean
    private UtilisateurRepository utilisateurRepository;


    /**
     * Test US01 - Affichage du formulaire de création
     * Doit retourner la vue avec un objet utilisateur et le token CSRF
     */
    @WithMockUser(username = "test", roles = { "USER" })
    @Test
    void afficherFormulaire_DoitRetournerVueAvecModel() throws Exception {
        mockMvc.perform(get("/api/utilisateurs/creer"))
                .andExpect(status().isOk())
                .andExpect(view().name("creerUtilisateur"))
                .andExpect(model().attributeExists("utilisateur"))
                .andExpect(model().attributeExists("_csrf"));
    }

    /**
     * US01 Test1 - Création réussie d'un nouveau profil
     * Doit rediriger vers la page de confirmation avec l'utilisateur créé
     */
    @WithMockUser(username = "test", roles = { "USER" })
    @Test
    void creerUtilisateur_QuandDonneesValides_DoitRetournerConfirmation() throws Exception {
        // Arrange
        Utilisateur mockUser = new Utilisateur();
        mockUser.setEmail("test@example.com");
        when(utilisateurService.creerUtilisateur(any(Utilisateur.class))).thenReturn(mockUser);

        // Act & Assert
        mockMvc.perform(post("/api/utilisateurs/creer")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // Important pour POST
                        .param("nom", "Doe")
                        .param("prenom", "John")
                        .param("email", "test@example.com")
                        .param("mdp", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("confirmationUtilisateur"))
                .andExpect(model().attributeExists("utilisateur"));
    }

    /**
     * US01 Test2 - Création avec un email existant
     * Doit retourner au formulaire avec message d'erreur
     */
    @WithMockUser(username = "test", roles = { "USER" })
    @Test
    void creerUtilisateur_QuandEmailExistant_DoitRetournerErreur() throws Exception {
        // Arrange
        String errorMessage = "Cet email est déjà utilisé !";
        when(utilisateurService.creerUtilisateur(any(Utilisateur.class)))
                .thenThrow(new IllegalArgumentException(errorMessage));

        // Act & Assert
        mockMvc.perform(post("/api/utilisateurs/creer")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // Important pour POST
                        .param("nom", "Dupont")
                        .param("prenom", "Alice")
                        .param("email", "existant@example.com")
                        .param("mdp", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("creerUtilisateur"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attribute("message", "Erreur : " + errorMessage));
    }


    
    //test pour le controller pour rechercher un utilisateur
    @WithMockUser(username = "test", roles = { "USER" })
    @Test
    void TestSearch() throws Exception {
        Utilisateur utilisateur = new Utilisateur("Alice", "Dupont", "alice@example.com", "1234");

        when(utilisateurService.rechercher("ali")).thenReturn(List.of(utilisateur));

        mockMvc.perform(get("/api/utilisateurs/search").param("q", "ali"))
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(model().attributeExists("resultats"))
                .andExpect(model().attribute("keyword", "ali"));
    }

}

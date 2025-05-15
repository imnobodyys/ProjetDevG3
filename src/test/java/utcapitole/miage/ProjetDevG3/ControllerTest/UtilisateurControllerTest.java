package utcapitole.miage.ProjetDevG3.ControllerTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import utcapitole.miage.projetDevG3.Controller.UtilisateurController;
import utcapitole.miage.projetDevG3.Service.UtilisateurService;
import utcapitole.miage.projetDevG3.model.Utilisateur;

@WebMvcTest(UtilisateurController.class)
@WithMockUser
public class UtilisateurControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UtilisateurService utilisateurService;

    /**
     * Test US01 - Affichage du formulaire de création
     * Doit retourner la vue avec un objet utilisateur et le token CSRF
     */
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
}

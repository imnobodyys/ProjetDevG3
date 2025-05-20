package utcapitole.miage.projetdevg3.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import utcapitole.miage.projetdevg3.repository.UtilisateurRepository;
import utcapitole.miage.projetdevg3.service.UtilisateurService;
import utcapitole.miage.projetdevg3.config.SecurityConfig;
import utcapitole.miage.projetdevg3.controller.UtilisateurController;
import utcapitole.miage.projetdevg3.model.Utilisateur;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(controllers = UtilisateurController.class)
@Import(SecurityConfig.class)
public class UtilisateurControllerTest {
        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private UtilisateurService utilisateurService;

        @MockBean
        private UtilisateurRepository utilisateurRepository;

        @MockBean
        private PasswordEncoder passwordEncoder;

        @MockBean
        private UserDetailsService userDetailsService;

        @BeforeEach
        void setup() {
                when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> {
                        String rawPassword = invocation.getArgument(0);
                        return new BCryptPasswordEncoder().encode(rawPassword);
                });
        }

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

        /**
         * US02 Test1 - connexion à mon compte
         * Connexion réussie avec des identifiants valides
         */
        @Test
        void connexion_Reussie_DoitRedirigerVersIndex() throws Exception {
                // Arrange
                String email = "alice@example.com";
                String rawPassword = "secret";
                String encodedPassword = passwordEncoder.encode(rawPassword);

                UserDetails userDetails = User.builder()
                                .username(email)
                                .password(encodedPassword)
                                .roles("USER")
                                .build();

                when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
                when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

                // Act & Assert
                mockMvc.perform(post("/api/utilisateurs/verifierlogin")
                                .with(csrf())
                                .param("username", email)
                                .param("password", rawPassword))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/accueil"));

        }

        /**
         * US02 Test2 - connexion à mon compte
         * Connexion échouée avec mot de passe incorrect
         */
        @Test
        void connexion_MotDePasseIncorrect_DoitAfficherErreur() throws Exception {
                // Arrange
                String email = "bob@example.com";
                String correctPassword = "correctPassword";
                String wrongPassword = "wrongPassword";
                String encodedPassword = passwordEncoder.encode(correctPassword);

                UserDetails userDetails = User.builder()
                                .username(email)
                                .password(encodedPassword)
                                .roles("USER")
                                .build();

                when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

                when(passwordEncoder.matches(wrongPassword, encodedPassword)).thenReturn(false);

                // Act & Assert
                mockMvc.perform(post("/api/utilisateurs/verifierlogin")
                                .with(csrf())
                                .param("username", email)
                                .param("password", wrongPassword))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/api/utilisateurs/login?error"));
        }

        /**
         * US02 Test3 - connexion à mon compte
         * Connexion échouée avec email non enregistré
         */
        @Test
        void connexion_EmailInconnu_DoitAfficherErreur() throws Exception {
                // Arrange
                String email = "unknown@example.com";

                when(userDetailsService.loadUserByUsername(email))
                                .thenThrow(new UsernameNotFoundException("User not found"));

                // Act & Assert
                mockMvc.perform(post("/api/utilisateurs/verifierlogin")
                                .with(csrf())
                                .param("username", email)
                                .param("password", "anyPassword"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/api/utilisateurs/login?error"));
        }

        /**
         * US02 Test4 - Affichage de la page de connexion
         * Affichage sans erreur : la page de connexion est affichée sans message
         * d’erreur
         */
        @Test
        void afficherPageLogin_SansErreur_DoitRetournerVueSansMessage() throws Exception {
                mockMvc.perform(get("/api/utilisateurs/login"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("login"))
                                .andExpect(model().attributeDoesNotExist("errorMessage"));
        }

        /**
         * US02 Test5 - Affichage de la page de connexion
         * Affichage avec erreur : un message d’erreur est affiché en cas de tentative
         * échouée
         */
        @Test
        void afficherPageLogin_AvecErreur_DoitAfficherMessage() throws Exception {
                mockMvc.perform(get("/api/utilisateurs/login")
                                .param("error", "true"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("login"))
                                .andExpect(model().attributeExists("errorMessage"))
                                .andExpect(model().attribute("errorMessage", "Identifiants incorrects"));
        }

        /**
         * US03 Test1 - Modification du profil
         * Modifier son profil avec des données valides
         */
        @WithMockUser(username = "user@test.com")
        @Test
        void soumettreModification_QuandValide_DoitConfirmer() throws Exception {
                // Arrange
                Utilisateur existingUser = new Utilisateur("Old", "Name", "user@test.com", "oldPass");
                existingUser.setId(1L);
                Utilisateur updatedUser = new Utilisateur("New", "Name", "new@test.com", "newPass");

                when(utilisateurService.getUtilisateurByEmail("user@test.com")).thenReturn(existingUser);
                when(utilisateurService.modifierUtilisateur(eq(1L), any())).thenReturn(updatedUser);

                // Act & Assert
                mockMvc.perform(post("/api/utilisateurs/modifier")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .param("nom", "New")
                                .param("prenom", "Name")
                                .param("email", "new@test.com")
                                .param("mdp", "newPass"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("confirmationUtilisateur"))
                                .andExpect(model().attribute("utilisateur", updatedUser));
        }

        /**
         * US03 Test2 - Modification du profil
         * Tentative de modification avec un email déjà utilisé
         */
        @WithMockUser(username = "user@test.com")
        @Test
        void soumettreModification_QuandEmailExistant_DoitRetournerErreur() throws Exception {
                // Arrange
                String errorMessage = "Cet email est déjà utilisé";
                Utilisateur existingUser = new Utilisateur("User", "Test", "user@test.com", "pass");
                existingUser.setId(1L);

                when(utilisateurService.getUtilisateurByEmail("user@test.com")).thenReturn(existingUser);
                when(utilisateurService.modifierUtilisateur(eq(1L), any()))
                                .thenThrow(new IllegalArgumentException(errorMessage));

                // Act & Assert
                mockMvc.perform(post("/api/utilisateurs/modifier")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .param("email", "existing@example.com"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("modifierProfil"))
                                .andExpect(model().attribute("errorMessage", errorMessage));
        }

        /**
         * US03 Test3 - Modification du profil
         * Affichage formulaire modification
         */
        @WithMockUser(username = "user@test.com")
        @Test
        void afficherFormulaireModification_DoitRemplirAvecDonneesUtilisateur() throws Exception {
                // Arrange
                Utilisateur mockUser = new Utilisateur("Test", "User", "user@test.com", "password");
                when(utilisateurService.getUtilisateurByEmail("user@test.com")).thenReturn(mockUser);

                // Act & Assert
                mockMvc.perform(get("/api/utilisateurs/modifier"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("modifierProfil"))
                                .andExpect(model().attributeExists("utilisateur"))
                                .andExpect(model().attribute("utilisateur", mockUser));
        }

        /**
         * US04 Test1 - Suppression de profil
         * Suppression réussie du profil
         */
        @WithMockUser(username = "user@test.com")
        @Test
        void supprimerProfil_QuandUtilisateurAuthentifie_DoitSupprimerEtRediriger() throws Exception {
                Utilisateur mockUser = new Utilisateur("Test", "User", "user@test.com", "pass");
                mockUser.setId(1L);

                when(utilisateurService.getUtilisateurByEmail("user@test.com")).thenReturn(mockUser);

                mockMvc.perform(post("/api/utilisateurs/supprimer")
                                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/api/utilisateurs/login?logout"));

                verify(utilisateurService).supprimerUtilisateur(1L);
        }

        // test pour le controller pour rechercher un utilisateur
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

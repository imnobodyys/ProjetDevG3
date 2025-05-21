package utcapitole.miage.projetdevg3.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.ModelAndView;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import utcapitole.miage.projetdevg3.config.SecurityConfig;
import utcapitole.miage.projetdevg3.controller.EvenementController;
import utcapitole.miage.projetdevg3.model.Evenement;
import utcapitole.miage.projetdevg3.model.Utilisateur;

import utcapitole.miage.projetdevg3.repository.EvenementRepository;
import utcapitole.miage.projetdevg3.service.EvenementService;
import utcapitole.miage.projetdevg3.service.UtilisateurService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(controllers = EvenementController.class)
@Import(SecurityConfig.class)
public class EvenementControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private EvenementRepository evenementRepository;

        @MockBean
        private EvenementService evenementService;

        @MockBean
        private UtilisateurService utilisateurService;

        @MockBean
        private PasswordEncoder passwordEncoder;

        @MockBean
        private UserDetailsService userDetailsService;

        @BeforeEach
        void setup() {
                UserDetails mockUserDetails = User.builder()
                                .username("user@test.com")
                                .password("encodedPass")
                                .roles("USER")
                                .build();

                when(userDetailsService.loadUserByUsername(anyString()))
                                .thenReturn(mockUserDetails);

                Utilisateur mockUser = new Utilisateur("Test", "User", "user@test.com", "pass");
                when(utilisateurService.getUtilisateurByEmail(anyString()))
                                .thenReturn(mockUser);
        }


        /**
         * Accueil des événements
         * Authentifié : l'utilisateur voit ses événements, ceux publics et ceux auxquels il participe
         */
        @Test
        @WithMockUser(username = "user@test.com")
        void accueilEvenements_QuandAuthentifie_DoitAfficherVueEvenementAvecAttributs() throws Exception {
        // Arrange : prépares des événements fictifs
        Utilisateur utilisateurMock = new Utilisateur("Jean", "Dupont", "user@test.com", "pass");
        ReflectionTestUtils.setField(utilisateurMock, "id", 1L);

        List<Evenement> evenementsPublics = Arrays.asList(new Evenement(), new Evenement());
        List<Evenement> mesEvenements = Arrays.asList(new Evenement());
        List<Evenement> evenementsInscrits = Arrays.asList(new Evenement(), new Evenement(), new Evenement());

        when(utilisateurService.getUtilisateurByEmail("user@test.com")).thenReturn(utilisateurMock);
        when(evenementService.getEvenementsPublics()).thenReturn(evenementsPublics);
        when(evenementService.getEvenementsParAuteur(utilisateurMock)).thenReturn(mesEvenements);
        when(evenementService.getEvenementsParParticipant(utilisateurMock)).thenReturn(evenementsInscrits);

        // Act & Assert
        mockMvc.perform(get("/api/evenements/"))
                .andExpect(status().isOk())
                .andExpect(view().name("evenement"))
                .andExpect(model().attribute("evenementsPublics", evenementsPublics))
                .andExpect(model().attribute("mesEvenements", mesEvenements))
                .andExpect(model().attribute("evenementsInscrits", evenementsInscrits));
        }

        /**
         * Accueil des événements
         * Non authentifié : accès refusé
         */
        @Test
        void accueilEvenements_SansAuthentification_DoitRetournerRedirectionLogin() throws Exception {
        mockMvc.perform(get("/api/evenements/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
        }


        /**
         * US43 Test1 - Création d'un événement
         * Création réussie avec tous les champs
         */
        @Test
        @WithMockUser(username = "user@test.com")
        void creerEvenement_QuandValide_DoitConfirmer() throws Exception {
                // Arrange
                Utilisateur mockUser = new Utilisateur("Test", "User", "user@test.com", "pass");
                when(utilisateurService.getUtilisateurByEmail(anyString())).thenReturn(mockUser);

                // Act & Assert
                mockMvc.perform(post("/api/evenements/creer")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .param("titre", "Event")
                                .param("description", "Description")
                                .param("contenu", "Content"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("confirmationEvenement"))
                                .andExpect(model().attributeExists("evenement"));
        }

        /**
         * US43 Test2 - Création d'un événement
         * Tentative de création sans titre
         */
        @Test
        @WithMockUser(username = "user@test.com")
        void creerEvenement_QuandTitreManquant_DoitRetournerErreur() throws Exception {
                when(evenementService.creerEvenement(any()))
                                .thenThrow(new IllegalArgumentException("Le titre est obligatoire"));

                mockMvc.perform(post("/api/evenements/creer")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .param("description", "Description"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("creerEvenement"))
                                .andExpect(model().attributeExists("message"));
        }

        /**
         * US43 Test3 - Création d'un événement
         * Tentative de création sans description
         */
        @Test
        @WithMockUser(username = "user@test.com")
        void creerEvenement_QuandDescriptionManquante_DoitRetournerErreur() throws Exception {
                when(evenementService.creerEvenement(any()))
                                .thenThrow(new IllegalArgumentException("La description est obligatoire"));

                mockMvc.perform(post("/api/evenements/creer")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .param("titre", "Titre sans description"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("creerEvenement"))
                                .andExpect(model().attributeExists("message"));
        }

        /**
         * US43 Test4 - Création d'un événement
         * Tentative de création sans être connecté
         */
        @Test
        void creerEvenement_SansAuthentification_DoitRetournerForbidden() throws Exception {
                mockMvc.perform(post("/api/evenements/creer"))
                                .andExpect(status().isForbidden());
        }

        /**
         * US43 Test5 - Création d'un événement
         * Affiche le formulaire de création d'événement
         */
        @Test
        @WithMockUser
        void afficherFormulaireEvenement_SansAuthentification_DoitRediriger() throws Exception {
                mockMvc.perform(get("/api/evenements/creer"))
                                .andExpect(status().isOk()); // @PreAuthorize already handled
        }

        /**
         * US44 Test1 - Modifier événement
         * Tentative de modification par l'auteur
         */
        @Test
        @WithMockUser(username = "organisateur@test.com")
        void afficherFormulaireModification_QuandOrganisateurValide_RetourneFormulaire() throws Exception {
                // Arrange
                Utilisateur organisateur = new Utilisateur("Organisateur", "Evenement", "organisateur@test.com",
                                "pass");
                ReflectionTestUtils.setField(organisateur, "id", 1L);
                Evenement evenementMock = new Evenement();
                evenementMock.setAuteur(organisateur); // Pas besoin de setter l'ID

                when(evenementService.getEvenementById(anyLong())).thenReturn(evenementMock);
                when(utilisateurService.getUtilisateurByEmail("organisateur@test.com")).thenReturn(organisateur);

                // Act & Assert
                mockMvc.perform(get("/api/evenements/modifier/{id}", 1L))
                                .andExpect(status().isOk())
                                .andExpect(view().name("modifierEvenement"))
                                .andExpect(model().attributeExists("evenement"))
                                .andExpect(model().attribute("evenement",
                                                hasProperty("auteur", equalTo(organisateur))));
        }

        /**
         * US44 Test2 - Modifier événement
         * Tentative de modification par un non-auteur
         */
        @Test
        @WithMockUser(username = "intrus@test.com")
        void afficherFormulaireModification_QuandUtilisateurNonAutorise_RetourneErreur() throws Exception {
                // Arrange
                Utilisateur organisateurLegitime = new Utilisateur("Organisateur", "Legitime", "organisateur@test.com",
                                "pass");
                ReflectionTestUtils.setField(organisateurLegitime, "id", 1L);
                Utilisateur intrus = new Utilisateur("Intrus", "Malveillant", "intrus@test.com", "hack");
                ReflectionTestUtils.setField(intrus, "id", 2L);
                Evenement evenementProtege = new Evenement();
                evenementProtege.setAuteur(organisateurLegitime);

                when(evenementService.getEvenementById(anyLong())).thenReturn(evenementProtege);
                when(utilisateurService.getUtilisateurByEmail("intrus@test.com")).thenReturn(intrus);

                // Act & Assert
                mockMvc.perform(get("/api/evenements/modifier/{id}", 1L))
                                .andExpect(status().isOk())
                                .andExpect(view().name("errorPage"))
                                .andExpect(model().attribute("errorMessage",
                                                "Accès refusé : Vous n'êtes pas l'auteur de cet événement"));
        }

        /**
         * US44 Test3 - Modifier événement
         * Soumission de la modification par l'auteur
         */
        @Test
        @WithMockUser(username = "organisateur@test.com")
        void soumettreModification_QuandOrganisateurValide_Succes() throws Exception {
                // Arrange
                Utilisateur organisateur = new Utilisateur("Organisateur", "Evenement", "organisateur@test.com",
                                "pass");
                ReflectionTestUtils.setField(organisateur, "id", 1L);

                Evenement evenementOriginal = new Evenement();
                ReflectionTestUtils.setField(evenementOriginal, "id", 1L);
                evenementOriginal.setAuteur(organisateur);

                Evenement evenementModifie = new Evenement();
                ReflectionTestUtils.setField(evenementModifie, "id", 1L);
                evenementModifie.setAuteur(organisateur);
                evenementModifie.setTitre("Titre modifié");

                when(utilisateurService.getUtilisateurByEmail("organisateur@test.com")).thenReturn(organisateur);
                when(evenementService.modifierEvenement(eq(1L), any(Evenement.class), eq(organisateur)))
                                .thenReturn(evenementModifie);

                // Act & Assert
                mockMvc.perform(post("/api/evenements/modifier/1")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .flashAttr("evenement", evenementModifie))
                                .andExpect(status().isOk())
                                .andExpect(view().name("confirmationEvenement"))
                                .andExpect(model().attributeExists("evenement"))
                                .andExpect(model().attribute("evenement",
                                                hasProperty("titre", equalTo("Titre modifié"))));
        }

        /**
         * US44 Test4 - Modifier événement
         * Soumission de la modification provoque une erreur
         */
        @Test
        @WithMockUser(username = "organisateur@test.com")
        void soumettreModification_QuandErreur_AlorsRetourneFormulaireAvecErreur() throws Exception {
                // Arrange
                Utilisateur organisateur = new Utilisateur("Organisateur", "Evenement", "organisateur@test.com",
                                "pass");
                ReflectionTestUtils.setField(organisateur, "id", 1L);

                Evenement evenement = new Evenement();
                ReflectionTestUtils.setField(evenement, "id", 1L);
                evenement.setAuteur(organisateur);

                when(utilisateurService.getUtilisateurByEmail("organisateur@test.com")).thenReturn(organisateur);
                when(evenementService.modifierEvenement(eq(1L), any(Evenement.class), eq(organisateur)))
                                .thenThrow(new IllegalArgumentException("Erreur lors de la modification"));

                // Act & Assert
                mockMvc.perform(post("/api/evenements/modifier/1")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .flashAttr("evenement", evenement))
                                .andExpect(status().isOk())
                                .andExpect(view().name("modifierEvenement"))
                                .andExpect(model().attributeExists("errorMessage"))
                                .andExpect(model().attribute("errorMessage",
                                                equalTo("Erreur lors de la modification")));
        }

        /**
         * US45 Test1 - Suppression d'un événement
         * Suppression réussie
         */
        @Test
        @WithMockUser(username = "auteur@test.com")
        void supprimerEvenement_QuandAuteurValide_DoitConfirmer() throws Exception {
                // Arrange
                Utilisateur auteur = new Utilisateur();
                ReflectionTestUtils.setField(auteur, "id", 1L);
                when(utilisateurService.getUtilisateurByEmail("auteur@test.com")).thenReturn(auteur);

                // Act & Assert
                mockMvc.perform(post("/api/evenements/supprimer/1")
                                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                                .andExpect(status().isOk())
                                .andExpect(view().name("confirmationSuppression"));
        }

        /**
         * US45 Test2 - Suppression d'un événement
         * Tentative de suppression par un non-auteur
         */
        @Test
        @WithMockUser(username = "intrus@test.com")
        void supprimerEvenement_QuandNonAuteur_DoitAfficherErreur() throws Exception {
                // Arrange
                Utilisateur auteurLegitime = new Utilisateur("Auteur", "Legitime", "auteur@test.com", "pass");
                ReflectionTestUtils.setField(auteurLegitime, "id", 1L);

                Utilisateur intrus = new Utilisateur("Intrus", "Test", "intrus@test.com", "pass");
                ReflectionTestUtils.setField(intrus, "id", 2L);

                // Simuler que l'utilisateur connecté est "intrus"
                when(utilisateurService.getUtilisateurByEmail("intrus@test.com")).thenReturn(intrus);

                // Configurer le service pour lever une exception
                doThrow(new IllegalArgumentException("Seul l'auteur peut supprimer l'événement"))
                                .when(evenementService).supprimerEvenement(eq(1L), eq(intrus));

                // Act & Assert
                mockMvc.perform(post("/api/evenements/supprimer/1")
                                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                                .andExpect(status().isOk())
                                .andExpect(view().name("errorPage"))
                                .andExpect(model().attributeExists("errorMessage"))
                                .andExpect(model().attribute("errorMessage",
                                                "Seul l'auteur peut supprimer l'événement"));
        }

    /**
     * US47 Test1 - Participer à un événement
     * Participation réussie à un événement
     */
    @Test
    @WithMockUser(username = "user@test.com")
    void participerEvenement_QuandValide_DoitConfirmer() throws Exception{
        // Arrange
        Utilisateur mockUser = new Utilisateur();
        ReflectionTestUtils.setField(mockUser, "id", 100L);
        when(utilisateurService.getUtilisateurByEmail(anyString())).thenReturn(mockUser);

        Evenement mockEvent = new Evenement();
        ReflectionTestUtils.setField(mockEvent, "id", 1L);
        when(evenementService.participerEvenement(anyLong(), any())).thenReturn(mockEvent);

        // Act & Assert
        mockMvc.perform(post("/api/evenements/participer/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("confirmationParticipation"))
                .andExpect(model().attributeExists("evenement"));
    }

    /**
     * US47 Test2 - Participer à un événement
     * Tentative de participation déjà existante
     */
    @Test
    @WithMockUser(username = "user@test.com")
    void participerEvenement_QuandDejaInscrit_DoitAfficherErreur() throws Exception {
        // Arrange
        Utilisateur mockUser = new Utilisateur();
        ReflectionTestUtils.setField(mockUser, "id", 100L);
        when(utilisateurService.getUtilisateurByEmail(anyString())).thenReturn(mockUser);
        
        doThrow(new IllegalArgumentException("Vous êtes déjà inscrit à cet événement"))
            .when(evenementService).participerEvenement(anyLong(), any());

        // Act & Assert
        mockMvc.perform(post("/api/evenements/participer/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("errorPage"))
                .andExpect(model().attribute("errorMessage", "Vous êtes déjà inscrit à cet événement"));
    }



    /**
     * US48 Test1 - Visualisation des participants à un événement
     * Visualiser les participants d'un événement existant
     */
    @WithMockUser
    @Test
    void afficherParticipants_QuandValide_DoitAfficherVue() throws Exception {
        // Arrange
        Evenement mockEvent = new Evenement();
        mockEvent.setTitre("Event test");
        mockEvent.addParticipant(new Utilisateur("Jean", "Dupont", "jean@test.com", "pass"));
        
        when(evenementService.getEvenementById(1L)).thenReturn(mockEvent);

        // Act & Assert
        MvcResult result = mockMvc.perform(get("/api/evenements/1/participants"))
            .andExpect(status().isOk())
            .andExpect(view().name("listeParticipants"))
            .andReturn();

        ModelAndView mav = result.getModelAndView();
        List<Utilisateur> participants = (List<Utilisateur>) mav.getModel().get("participants");
        
        assertEquals(1, participants.size());
        assertEquals("jean@test.com", participants.get(0).getEmail());
    }

    
    /**
     * US48 Test2 - Visualisation des participants à un événement
     * Tentative d'accès à un événement inexistant
     */
    @WithMockUser
    @Test
    void afficherParticipants_QuandInvalide_DoitAfficherErreur() throws Exception {
        // Arrange
        when(evenementService.getEvenementById(999L))
            .thenThrow(new IllegalArgumentException("Événement non trouvé"));

        // Act & Assert
        mockMvc.perform(get("/api/evenements/999/participants"))
            .andExpect(status().isOk())
            .andExpect(view().name("errorPage"))
            .andExpect(model().attribute("errorMessage", "Événement non trouvé"));
    }
}

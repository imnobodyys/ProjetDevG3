package utcapitole.miage.projetdevg3.controller;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import utcapitole.miage.projetdevg3.service.DemandeAmiService;
import utcapitole.miage.projetdevg3.config.SecurityConfig;
import utcapitole.miage.projetdevg3.controller.DemandeAmiController;
import utcapitole.miage.projetdevg3.model.DemandeAmi;
import utcapitole.miage.projetdevg3.model.StatutDemande;
import utcapitole.miage.projetdevg3.model.Utilisateur;

import utcapitole.miage.projetdevg3.repository.UtilisateurRepository;

@WebMvcTest(controllers = DemandeAmiController.class)
@Import(SecurityConfig.class)
class DemandeAmiControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private DemandeAmiService demandeAmiService;

        @MockBean
        private UtilisateurRepository utilisateurRepository;

        @Autowired
        private DemandeAmiController controller;

        @Mock
        private RedirectAttributes redirectAttributes;

        private Utilisateur mockUser;
        private DemandeAmi mockDemande;

        @BeforeEach
        void setUp() {
                MockitoAnnotations.openMocks(this);
                mockUser = new Utilisateur();
                mockUser.setId(1L);
                mockUser.setEmail("test@example.com");
                mockUser.setNom("Test User");

                mockDemande = new DemandeAmi();
                mockDemande.setId(1L);
                mockDemande.setExpediteurAmi(mockUser);
                mockDemande.setDtEnvoi(LocalDateTime.now());
                mockDemande.setStatut(StatutDemande.EN_ATTENTE);
        }

        @Test
        @WithMockUser(username = "test@example.com")
        void envoyerDemande_Success() throws Exception {
                Long destinataireId = 2L;

                when(utilisateurRepository.findByEmail("test@example.com"))
                                .thenReturn(Optional.of(mockUser));

                mockMvc.perform(post("/demandes/envoyer")
                                .param("destinataireId", destinataireId.toString())
                                .with(csrf()))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/users"))
                                .andExpect(flash().attribute("success", "Demande envoyée avec succès !"));

                verify(demandeAmiService).envoyerDemandeAmi(1L, destinataireId);
        }

        @Test
        @WithMockUser(username = "test@example.com")
        void testEnvoyerDemande_userNotFound() {
                Principal principal = () -> "notfound@example.com";
                when(utilisateurRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

                UsernameNotFoundException exception = assertThrows(
                                UsernameNotFoundException.class,
                                () -> controller.envoyerDemande(2L, principal, redirectAttributes));

                assertEquals("Utilisateur non trouvé", exception.getMessage());
        }

        @Test
        @WithMockUser(username = "test@example.com")
        void getDemandesRecues_ShouldReturnViewAndModel() throws Exception {
                when(utilisateurRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
                when(demandeAmiService.getDemandesRecuesEnAttente(mockUser)).thenReturn(List.of(mockDemande));

                mockMvc.perform(get("/demandes/recues"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("demandes-recues"))
                                .andExpect(model().attributeExists("demandes"));
        }

        @Test
        @WithMockUser(username = "test@example.com")
        void accepterDemande_ShouldRedirect() throws Exception {
                when(utilisateurRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));

                mockMvc.perform(post("/demandes/accepter/1").with(csrf()))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/demandes/recues"));

                verify(demandeAmiService).accepterDemande(1L, mockUser);
        }

        @Test
        @WithMockUser(username = "test@example.com")
        void refuserDemande_ShouldRedirect() throws Exception {
                when(utilisateurRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));

                mockMvc.perform(post("/demandes/refuser/1").with(csrf()))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/demandes/recues"));

                verify(demandeAmiService).refuserDemande(1L, mockUser);
        }

        @Test
        @WithMockUser(username = "test@example.com")
        void voirMesAmis_ShouldReturnAmisViewWithFriendsList() throws Exception {
                Utilisateur ami1 = new Utilisateur();
                ami1.setEmail("ami1@example.com");
                Utilisateur ami2 = new Utilisateur();
                ami2.setEmail("ami2@example.com");

                when(utilisateurRepository.findByEmail("test@example.com"))
                                .thenReturn(Optional.of(mockUser));

                when(demandeAmiService.getAmis(mockUser))
                                .thenReturn(Arrays.asList(ami1, ami2));

                mockMvc.perform(get("/demandes/amis"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("amis"))
                                .andExpect(model().attributeExists("amis"))
                                .andExpect(model().attribute("amis", hasSize(2)));

                verify(utilisateurRepository).findByEmail("test@example.com");
                verify(demandeAmiService).getAmis(mockUser);
        }

        @Test
        @WithMockUser(username = "test@example.com")
        void supprimerAmi_ShouldRedirect() throws Exception {
                Long amiId = 2L;
                when(utilisateurRepository.findByEmail("test@example.com"))
                                .thenReturn(Optional.of(mockUser));

                mockMvc.perform(post("/demandes/supprimer/{id}", amiId).with(csrf()))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/demandes/amis"));

                verify(demandeAmiService).supprimerAmi(mockUser, amiId);
        }
}

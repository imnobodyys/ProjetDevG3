
package utcapitole.miage.projetDevG3.controller;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import utcapitole.miage.projetDevG3.Controller.DemandeAmiController;
import utcapitole.miage.projetDevG3.Service.DemandeAmiService;
import utcapitole.miage.projetDevG3.config.SecurityConfig;
import utcapitole.miage.projetDevG3.model.DemandeAmi;
import utcapitole.miage.projetDevG3.model.StatutDemande;
import utcapitole.miage.projetDevG3.model.Utilisateur;
import utcapitole.miage.projetDevG3.Repository.UtilisateurRepository;

@WebMvcTest(controllers = DemandeAmiController.class)
@Import(SecurityConfig.class)
public class DemandeAmiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DemandeAmiService demandeAmiService;

    @MockitoBean
    private UtilisateurRepository utilisateurRepository;

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

    /**
     * test pour envoyer une demande
     * 
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "test@example.com")
    void envoyerDemande_Success() throws Exception {

        Long destinataireId = 2L;
        Utilisateur utilisateur1 = new Utilisateur();
        utilisateur1.setId(1L);
        utilisateur1.setEmail("test@example.com");

        when(utilisateurRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(utilisateur1));

        mockMvc.perform(post("/demandes/envoyer")
                .param("destinataireId", destinataireId.toString())
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"))
                .andExpect(flash().attribute("success", "sucess! "));

        verify(demandeAmiService).envoyerdemandeami(1L, destinataireId);
    }

    /**
     * test pour voir envoyer une demande mais il y a pas de id
     */
    @Test
    @WithMockUser(username = "test@example.com")
    void testEnvoyerDemande_userNotFound() {
        Principal principal = () -> "notfound@example.com";
        when(utilisateurRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        DemandeAmiController controller = new DemandeAmiController(null, null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            controller.envoyerDemande(2L, principal, redirectAttributes);
        });

        assertEquals("Utilisateur non trouvé", exception.getMessage());
    }

    /**
     * test pour voir list de recues
     * 
     * @throws Exception
     */
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

    /**
     * test pour accepter demande
     * 
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "test@example.com")
    void accepterDemande_ShouldRedirect() throws Exception {
        when(utilisateurRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));

        mockMvc.perform(post("/demandes/accepter/1").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/demandes/recues"));

        verify(demandeAmiService).accepterDemande(1L, mockUser);
    }

    /**
     * test pour refuser demande
     * 
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "test@example.com")
    void refuserDemande_ShouldRedirect() throws Exception {
        when(utilisateurRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));

        mockMvc.perform(post("/demandes/refuser/1").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/demandes/recues"));

        verify(demandeAmiService).refuserDemande(1L, mockUser);
    }

    /**
     * test pour voir mes amis
     * 
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "test@example.com")
    void voirMesAmis_ShouldReturnAmisViewWithFriendsList() throws Exception {
        // Arrange
        Utilisateur currentUser = new Utilisateur();
        currentUser.setEmail("test@example.com");

        Utilisateur ami1 = new Utilisateur();
        ami1.setEmail("ami1@example.com");

        Utilisateur ami2 = new Utilisateur();
        ami2.setEmail("ami2@example.com");

        when(utilisateurRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(currentUser));

        when(demandeAmiService.getAmis(currentUser))
                .thenReturn(Arrays.asList(ami1, ami2));

        // Act & Assert
        mockMvc.perform(get("/demandes/amis"))
                .andExpect(status().isOk())
                .andExpect(view().name("amis"))
                .andExpect(model().attributeExists("amis"))
                .andExpect(model().attribute("amis", hasSize(2)));

        verify(utilisateurRepository, times(1)).findByEmail("test@example.com");
        verify(demandeAmiService, times(1)).getAmis(currentUser);
    }

    /**
     * test pour supprimer un ami
     * 
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "test@example.com")
    void supprimerAmi_ShouldRedirect() throws Exception {
        // Arrange
        Long amiId = 2L;
        Utilisateur currentUser = new Utilisateur();
        currentUser.setId(1L);
        currentUser.setEmail("test@example.com");

        when(utilisateurRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(currentUser));

        // Act & Assert
        mockMvc.perform(post("/demandes/supprier/{id}", amiId)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/demandes/amis"));

        // Verify service method called correctly
        verify(demandeAmiService, times(1)).supprimeramis(currentUser, amiId);
    }

}

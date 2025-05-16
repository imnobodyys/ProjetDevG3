package utcapitole.miage.projetDevG3.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.security.Principal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import utcapitole.miage.projetDevG3.Controller.DemandeAmiController;
import utcapitole.miage.projetDevG3.Service.DemandeAmiService;
import utcapitole.miage.projetDevG3.model.Utilisateur;
import utcapitole.miage.projetDevG3.Repository.UtilisateurRepository;

public class DemandeAmiControllerTest {

    @Mock
    private DemandeAmiService demandeAmiService;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private Principal principal;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private DemandeAmiController demandeAmiController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * test pour verifier envoyer demande succes
     */
    @Test
    public void testEnvoyerDemande_success() {

        Long destinataireId = 2L;
        String email = "jean@example.com";
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(1L);
        utilisateur.setEmail(email);

        when(principal.getName()).thenReturn(email);
        when(utilisateurRepository.findByEmail(email)).thenReturn(Optional.of(utilisateur));

        when(redirectAttributes.addFlashAttribute(eq("success"), anyString())).thenReturn(redirectAttributes);

        String viewName = demandeAmiController.envoyerDemande(destinataireId, principal, redirectAttributes);

        verify(utilisateurRepository, times(1)).findByEmail(email);
        verify(demandeAmiService, times(1)).envoyerdemandeami(utilisateur.getId(), destinataireId);
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("success"), anyString());

        assertEquals("redirect:/users", viewName);
    }

    /**
     * test pour verifier envoyer demande quand user not found
     */
    @Test
    public void testEnvoyerDemande_userNotFound() {

        Long destinataireId = 3L;
        String email = "notfound@example.com";

        when(principal.getName()).thenReturn(email);
        when(utilisateurRepository.findByEmail(email)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            demandeAmiController.envoyerDemande(destinataireId, principal, redirectAttributes);
        });

        assertEquals("Utilisateur non trouvé", exception.getMessage());
    }
}

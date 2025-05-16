package utcapitole.miage.projetDevG3.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import utcapitole.miage.projetDevG3.Repository.DemandeAmiRepository;
import utcapitole.miage.projetDevG3.Repository.UtilisateurRepository;
import utcapitole.miage.projetDevG3.model.DemandeAmi;
import utcapitole.miage.projetDevG3.model.Utilisateur;

public class DemandeAmiServiceTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private DemandeAmiRepository demandeAmiRepository;

    @InjectMocks
    private DemandeAmiService demandeAmiService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this); // 初始化 mocks
    }

    @Test
    public void testEnvoyerDemandeAmi_selfRequest_throwsException() {
        Long userId = 1L;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            demandeAmiService.envoyerdemandeami(userId, userId);
        });

        assertEquals("ne peuvez pas ajourer vous meme", exception.getMessage());
    }

    @Test
    public void testEnvoyerDemandeAmi_duplicateRequest_throwsException() {
        Long expediteur = 1L;
        Long destinaire = 2L;

        when(demandeAmiRepository.existsDemandeBetween(expediteur,
                destinaire))
                .thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            demandeAmiService.envoyerdemandeami(expediteur, destinaire);
        });

        assertEquals("Deja demande", exception.getMessage());
    }

    @Test
    public void testEnvoyerDemandeAmi_validRequest_savesDemande() {
        Long expediteur = 1L;
        Long destinaire = 2L;

        Utilisateur user1 = new Utilisateur();
        user1.setId(expediteur);
        Utilisateur user2 = new Utilisateur();
        user2.setId(destinaire);

        when(demandeAmiRepository.existsDemandeBetween(expediteur,
                destinaire))
                .thenReturn(false);
        when(utilisateurRepository.getReferenceById(expediteur)).thenReturn(user1);
        when(utilisateurRepository.getReferenceById(destinaire)).thenReturn(user2);

        demandeAmiService.envoyerdemandeami(expediteur, destinaire);

        verify(demandeAmiRepository, times(1)).save(any(DemandeAmi.class));
    }
}

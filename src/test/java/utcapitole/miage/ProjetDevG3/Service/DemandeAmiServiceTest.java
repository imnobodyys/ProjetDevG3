package utcapitole.miage.projetDevG3.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import utcapitole.miage.projetDevG3.Repository.DemandeAmiRepository;
import utcapitole.miage.projetDevG3.Repository.UtilisateurRepository;
import utcapitole.miage.projetDevG3.model.DemandeAmi;
import utcapitole.miage.projetDevG3.model.StatutDemande;
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

    /**
     * test pour service de Demande ami moi meme
     */
    @Test
    public void testEnvoyerDemandeAmi_selfRequest_throwsException() {
        Long userId = 1L;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            demandeAmiService.envoyerdemandeami(userId, userId);
        });

        assertEquals("ne peuvez pas ajourer vous meme", exception.getMessage());
    }

    /**
     * 
     * test pour envoyer demande a meme person
     */
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

    /**
     * test pour reussir demande ami
     */
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

    /**
     * test pour demandes recues
     */
    @Test
    void testGetDemandesRecuesEnAttente_ShouldReturnList() {
        // Arrange
        Utilisateur destinataire = new Utilisateur();
        destinataire.setId(1L);
        destinataire.setEmail("test@example.com");

        DemandeAmi demande = new DemandeAmi();
        demande.setId(1L);
        demande.setStatut(StatutDemande.EN_ATTENTE);
        demande.setDtEnvoi(LocalDateTime.now());
        demande.setDestinataireAmi(destinataire);

        when(demandeAmiRepository.findByDestinataireAmiAndStatut(destinataire, StatutDemande.EN_ATTENTE))
                .thenReturn(List.of(demande));

        // Act
        List<DemandeAmi> result = demandeAmiService.getDemandesRecuesEnAttente(destinataire);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(StatutDemande.EN_ATTENTE, result.get(0).getStatut());
        assertEquals(destinataire, result.get(0).getDestinataireAmi());
        verify(demandeAmiRepository, times(1)).findByDestinataireAmiAndStatut(destinataire, StatutDemande.EN_ATTENTE);
    }

    /**
     * test pour voir list des amis
     */
    @Test
    void getAmis_ShouldReturnFriendsList() {
        // Arrange
        Utilisateur currentUser = new Utilisateur();
        currentUser.setId(1L);
        currentUser.setEmail("user@example.com");

        Utilisateur friend1 = new Utilisateur();
        friend1.setId(2L);
        friend1.setEmail("friend1@example.com");

        Utilisateur friend2 = new Utilisateur();
        friend2.setId(3L);
        friend2.setEmail("friend2@example.com");

        DemandeAmi demande1 = new DemandeAmi();
        demande1.setExpediteurAmi(currentUser);
        demande1.setDestinataireAmi(friend1);
        demande1.setStatut(StatutDemande.ACCEPTE);

        DemandeAmi demande2 = new DemandeAmi();
        demande2.setExpediteurAmi(friend2);
        demande2.setDestinataireAmi(currentUser);
        demande2.setStatut(StatutDemande.ACCEPTE);

        when(demandeAmiRepository.findByStatutAndDestinataireAmiOrStatutAndExpediteurAmi(
                StatutDemande.ACCEPTE, currentUser,
                StatutDemande.ACCEPTE, currentUser))
                .thenReturn(Arrays.asList(demande1, demande2));

        // Act
        List<Utilisateur> amis = demandeAmiService.getAmis(currentUser);

        // Assert
        assertEquals(2, amis.size());
        assertTrue(amis.contains(friend1));
        assertTrue(amis.contains(friend2));

        verify(demandeAmiRepository, times(1))
                .findByStatutAndDestinataireAmiOrStatutAndExpediteurAmi(
                        StatutDemande.ACCEPTE, currentUser,
                        StatutDemande.ACCEPTE, currentUser);
    }
}

package utcapitole.miage.projetDevG3.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.*;
import org.mockito.*;

import utcapitole.miage.projetDevG3.Repository.DemandeAmiRepository;
import utcapitole.miage.projetDevG3.Repository.UtilisateurRepository;
import utcapitole.miage.projetDevG3.model.DemandeAmi;
import utcapitole.miage.projetDevG3.model.StatutDemande;
import utcapitole.miage.projetDevG3.model.Utilisateur;

class DemandeAmiServiceTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private DemandeAmiRepository demandeAmiRepository;

    @InjectMocks
    private DemandeAmiService demandeAmiService;

    private Utilisateur currentUser;
    private Utilisateur otherUser;
    private DemandeAmi pendingDemande;

    /**
     * Initialisation des données de test avant chaque méthode
     */
    @BeforeEach
    void setUp() {
        currentUser = new Utilisateur();
        currentUser.setId(1L);
        currentUser.setEmail("current@example.com");

        otherUser = new Utilisateur();
        otherUser.setId(2L);
        otherUser.setEmail("other@example.com");

        pendingDemande = new DemandeAmi();
        pendingDemande.setId(100L);
        pendingDemande.setStatut(StatutDemande.EN_ATTENTE);
        pendingDemande.setDestinataireAmi(currentUser);
        pendingDemande.setExpediteurAmi(otherUser);
    }

    /**
     * Teste qu'une demande à soi-même lève une exception
     */
    @Test
    void demandeASoiMeme_DevraitLeverException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> demandeAmiService.envoyerdemandeami(1L, 1L));
        assertEquals("ne peuvez pas ajourer vous meme", exception.getMessage());
    }

    /**
     * Teste qu'une demande en double lève une exception
     */
    @Test
    void demandeExistante_DevraitLeverException() {
        when(demandeAmiRepository.existsDemandeBetween(1L, 2L))
                .thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> demandeAmiService.envoyerdemandeami(1L, 2L));
        assertEquals("Deja demande", exception.getMessage());
    }

    /**
     * Teste qu'une demande valide est sauvegardée
     */
    @Test
    void demandeValide_DevraitSauvegarder() {
        when(demandeAmiRepository.existsDemandeBetween(1L, 2L))
                .thenReturn(false);
        when(utilisateurRepository.getReferenceById(1L))
                .thenReturn(currentUser);
        when(utilisateurRepository.getReferenceById(2L))
                .thenReturn(otherUser);

        demandeAmiService.envoyerdemandeami(1L, 2L);

        verify(demandeAmiRepository).save(any(DemandeAmi.class));
    }

    /**
     * Teste la récupération des demandes en attente
     */
    @Test
    void devraitRetournerDemandesEnAttente() {
        when(demandeAmiRepository.findByDestinataireAmiAndStatut(
                currentUser, StatutDemande.EN_ATTENTE))
                .thenReturn(List.of(pendingDemande));

        List<DemandeAmi> result = demandeAmiService
                .getDemandesRecuesEnAttente(currentUser);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1, result.size()),
                () -> assertEquals(pendingDemande, result.get(0)));
    }

    /**
     * Teste l'acceptation d'une demande valide
     */
    @Test
    void devraitAccepterDemandeValide() {
        when(demandeAmiRepository.findById(100L))
                .thenReturn(Optional.of(pendingDemande));

        demandeAmiService.accepterDemande(100L, currentUser);

        assertAll(
                () -> assertEquals(StatutDemande.ACCEPTE, pendingDemande.getStatut()),
                () -> verify(demandeAmiRepository).save(pendingDemande));
    }

    /**
     * Teste le refus si l'utilisateur n'est pas le destinataire
     */
    @Test
    void devraitRefuserSiPasDestinataire() {
        when(demandeAmiRepository.findById(100L))
                .thenReturn(Optional.of(pendingDemande));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> demandeAmiService.accepterDemande(100L, otherUser));
        assertEquals("vous n'avez pas drois", exception.getMessage());
    }

    /**
     * Teste la récupération de la liste d'amis
     */
    @Test
    void devraitRetournerListeAmis() {
        DemandeAmi demandeAcceptee1 = new DemandeAmi();
        demandeAcceptee1.setExpediteurAmi(currentUser);
        demandeAcceptee1.setDestinataireAmi(otherUser);
        demandeAcceptee1.setStatut(StatutDemande.ACCEPTE);

        when(demandeAmiRepository.findByStatutAndDestinataireAmiOrStatutAndExpediteurAmi(
                StatutDemande.ACCEPTE, currentUser,
                StatutDemande.ACCEPTE, currentUser))
                .thenReturn(List.of(demandeAcceptee1));

        List<Utilisateur> amis = demandeAmiService.getAmis(currentUser);

        assertEquals(1, amis.size());
        assertEquals(otherUser, amis.get(0));
    }
}
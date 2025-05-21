package utcapitole.miage.projetdevg3.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import utcapitole.miage.projetdevg3.model.DemandeAmi;
import utcapitole.miage.projetdevg3.model.StatutDemande;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.repository.DemandeAmiRepository;
import utcapitole.miage.projetdevg3.repository.UtilisateurRepository;
import utcapitole.miage.projetdevg3.service.DemandeAmiService;

@ExtendWith(MockitoExtension.class)
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

        @Test
        void demandeASoiMeme_DevraitLeverException() {
                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> demandeAmiService.envoyerDemandeAmi(1L, 1L));

                assertEquals("Vous ne pouvez pas vous ajouter vous-même comme ami", exception.getMessage());
        }

        @Test
        void demandeExistante_DevraitLeverException() {
                when(demandeAmiRepository.existsDemandeBetween(1L, 2L)).thenReturn(true);

                IllegalStateException exception = assertThrows(
                                IllegalStateException.class,
                                () -> demandeAmiService.envoyerDemandeAmi(1L, 2L));

                assertEquals("Une demande existe déjà entre ces utilisateurs", exception.getMessage());
                verify(demandeAmiRepository).existsDemandeBetween(1L, 2L);
        }

        @Test
        void demandeValide_DevraitSauvegarder() {
                when(demandeAmiRepository.existsDemandeBetween(1L, 2L)).thenReturn(false);
                when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(currentUser));
                when(utilisateurRepository.findById(2L)).thenReturn(Optional.of(otherUser));

                demandeAmiService.envoyerDemandeAmi(1L, 2L);

                verify(demandeAmiRepository).save(any(DemandeAmi.class));
        }

        @Test
        void devraitRetournerDemandesEnAttente() {
                when(demandeAmiRepository.findByDestinataireAmiAndStatut(
                                currentUser, StatutDemande.EN_ATTENTE)).thenReturn(List.of(pendingDemande));

                List<DemandeAmi> result = demandeAmiService.getDemandesRecuesEnAttente(currentUser);

                assertAll(
                                () -> assertNotNull(result),
                                () -> assertEquals(1, result.size()),
                                () -> assertEquals(pendingDemande, result.get(0)));
        }

        @Test
        void devraitAccepterDemandeValide() {
                when(demandeAmiRepository.findById(100L)).thenReturn(Optional.of(pendingDemande));

                demandeAmiService.accepterDemande(100L, currentUser);

                assertAll(
                                () -> assertEquals(StatutDemande.ACCEPTE, pendingDemande.getStatut()),
                                () -> verify(demandeAmiRepository).save(pendingDemande));
        }

        @Test
        void refuserDemande_valideDemande_devraitChangerStatut() {
                when(demandeAmiRepository.findById(100L)).thenReturn(Optional.of(pendingDemande));

                demandeAmiService.refuserDemande(100L, currentUser);

                assertEquals(StatutDemande.REFUSE, pendingDemande.getStatut());
                verify(demandeAmiRepository).save(pendingDemande);
        }

        @Test
        void devraitRefuserSiPasDestinataire() {
                when(demandeAmiRepository.findById(100L)).thenReturn(Optional.of(pendingDemande));

                IllegalStateException exception = assertThrows(
                                IllegalStateException.class,
                                () -> demandeAmiService.accepterDemande(100L, otherUser));

                assertEquals("Vous n'avez pas le droit d'accepter cette demande", exception.getMessage());
        }

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

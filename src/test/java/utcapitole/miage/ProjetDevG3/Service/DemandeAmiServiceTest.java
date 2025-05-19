package utcapitole.miage.projetDevG3.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import utcapitole.miage.projetDevG3.Repository.DemandeAmiRepository;
import utcapitole.miage.projetDevG3.Repository.UtilisateurRepository;
import utcapitole.miage.projetDevG3.model.DemandeAmi;
import utcapitole.miage.projetDevG3.model.StatutDemande;
import utcapitole.miage.projetDevG3.model.Utilisateur;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Classe de test unitaire pour le service {@link DemandeAmiService}.
 * Teste les fonctionnalités liées à la gestion des demandes d'amis.
 */
@ExtendWith(MockitoExtension.class)
class DemandeAmiServiceTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private DemandeAmiRepository demandeAmiRepository;

    @InjectMocks
    private DemandeAmiService demandeAmiService;

    private Utilisateur utilisateurActuel;
    private Utilisateur utilisateurAmi;
    private DemandeAmi demande;

    @BeforeEach
    void initialiser() {
        utilisateurActuel = new Utilisateur();
        utilisateurActuel.setId(1L);
        utilisateurActuel.setEmail("current@example.com");

        utilisateurAmi = new Utilisateur();
        utilisateurAmi.setId(2L);
        utilisateurAmi.setEmail("friend@example.com");

        demande = new DemandeAmi();
        demande.setId(100L);
        demande.setExpediteurAmi(utilisateurAmi);
        demande.setDestinataireAmi(utilisateurActuel);
        demande.setStatut(StatutDemande.EN_ATTENTE);
        demande.setDtEnvoi(LocalDateTime.now());
    }

    /**
     * Test l'envoi d'une demande à soi-même. Doit lever une exception.
     */
    @Test
    void envoyerDemandeAMoiMeme_devraitEchouer() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> demandeAmiService.envoyerdemandeami(1L, 1L));

        assertEquals("ne peuvez pas ajourer vous meme", exception.getMessage());
    }

    /**
     * Test l'envoi d'une demande déjà existante. Doit lever une exception.
     */
    @Test
    void envoyerDemandeEnDouble_devraitEchouer() {
        when(demandeAmiRepository.existsDemandeBetween(1L, 2L))
                .thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> demandeAmiService.envoyerdemandeami(1L, 2L));

        assertEquals("Deja demande", exception.getMessage());
        verify(demandeAmiRepository).existsDemandeBetween(1L, 2L);
    }

    /**
     * Test l'envoi valide d'une demande d'ami.
     */
    @Test
    void envoyerDemandeValide_devraitSauvegarder() {
        when(demandeAmiRepository.existsDemandeBetween(1L, 2L)).thenReturn(false);
        when(utilisateurRepository.getReferenceById(1L)).thenReturn(utilisateurActuel);
        when(utilisateurRepository.getReferenceById(2L)).thenReturn(utilisateurAmi);

        demandeAmiService.envoyerdemandeami(1L, 2L);
        ArgumentCaptor<DemandeAmi> captor = ArgumentCaptor.forClass(DemandeAmi.class);
        verify(demandeAmiRepository).save(captor.capture());

        DemandeAmi demandeSauvegardee = captor.getValue();
        assertEquals(utilisateurActuel, demandeSauvegardee.getExpediteurAmi());
        assertEquals(utilisateurAmi, demandeSauvegardee.getDestinataireAmi());
        assertEquals(StatutDemande.EN_ATTENTE, demandeSauvegardee.getStatut());
        assertNotNull(demandeSauvegardee.getDtEnvoi());

    }

    /**
     * Teste la récupération des demandes en attente reçues.
     */
    @Test
    void recupererDemandesEnAttente_devraitRetournerListe() {
        when(demandeAmiRepository.findByDestinataireAmiAndStatut(utilisateurActuel, StatutDemande.EN_ATTENTE))
                .thenReturn(List.of(demande));

        List<DemandeAmi> resultats = demandeAmiService.getDemandesRecuesEnAttente(utilisateurActuel);

        assertNotNull(resultats);
        assertEquals(1, resultats.size());
        assertEquals(demande, resultats.get(0));
        verify(demandeAmiRepository).findByDestinataireAmiAndStatut(utilisateurActuel, StatutDemande.EN_ATTENTE);
    }

    /**
     * Test la récupération des demandes lorsqu'aucune demande en attente n'existe.
     */
    @Test
    void aucunesDemandesEnAttente_devraitRetournerListeVide() {
        when(demandeAmiRepository.findByDestinataireAmiAndStatut(utilisateurActuel, StatutDemande.EN_ATTENTE))
                .thenReturn(List.of());

        List<DemandeAmi> resultats = demandeAmiService.getDemandesRecuesEnAttente(utilisateurActuel);

        assertTrue(resultats.isEmpty());
    }

    /**
     * Test la récupération des amis après acceptation de demandes.
     */
    @Test
    void recupererAmis_devraitRetournerListeAmis() {
        DemandeAmi demande1 = new DemandeAmi();
        demande1.setExpediteurAmi(utilisateurActuel);
        demande1.setDestinataireAmi(utilisateurAmi);
        demande1.setStatut(StatutDemande.ACCEPTE);

        Utilisateur autreAmi = new Utilisateur();
        autreAmi.setId(3L);

        DemandeAmi demande2 = new DemandeAmi();
        demande2.setExpediteurAmi(autreAmi);
        demande2.setDestinataireAmi(utilisateurActuel);
        demande2.setStatut(StatutDemande.ACCEPTE);

        when(demandeAmiRepository.findByStatutAndDestinataireAmiOrStatutAndExpediteurAmi(
                StatutDemande.ACCEPTE, utilisateurActuel,
                StatutDemande.ACCEPTE, utilisateurActuel))
                .thenReturn(Arrays.asList(demande1, demande2));

        List<Utilisateur> amis = demandeAmiService.getAmis(utilisateurActuel);

        assertEquals(2, amis.size());
        assertTrue(amis.contains(utilisateurAmi));
        assertTrue(amis.contains(autreAmi));
    }

    /**
     * Test pour les demandes non acceptées ne sont pas considérées comme amis.
     */
    @Test
    void demandesNonAcceptees_neDoiventPasEtreDansListeAmis() {
        when(demandeAmiRepository.findByStatutAndDestinataireAmiOrStatutAndExpediteurAmi(
                StatutDemande.ACCEPTE, utilisateurActuel,
                StatutDemande.ACCEPTE, utilisateurActuel))
                .thenReturn(List.of());

        List<Utilisateur> amis = demandeAmiService.getAmis(utilisateurActuel);

        assertTrue(amis.isEmpty());
    }

    /**
     * Test pour accepter une demande valide.
     */
    @Test
    void accepterDemandeValide_devraitChangerStatutEtSauvegarder() {
        when(demandeAmiRepository.findById(100L)).thenReturn(Optional.of(demande));

        demandeAmiService.accepterDemande(100L, utilisateurActuel);

        assertEquals(StatutDemande.ACCEPTE, demande.getStatut());
        verify(demandeAmiRepository).save(demande);
    }

}

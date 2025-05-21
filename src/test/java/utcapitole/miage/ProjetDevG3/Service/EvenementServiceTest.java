package utcapitole.miage.projetdevg3.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import utcapitole.miage.projetdevg3.model.*;
import utcapitole.miage.projetdevg3.repository.EvenementRepository;
import utcapitole.miage.projetdevg3.service.EvenementService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EvenementServiceTest {

    @Mock
    private EvenementRepository evenementRepository;

    @InjectMocks
    private EvenementService evenementService;

    /**
     * Teste la récupération d'un événement existant par ID.
     */
    @Test
    void testGetEvenementById_Success() {
        Long evenementId = 1L;

        Evenement evenement = new Evenement();
        ReflectionTestUtils.setField(evenement, "id", evenementId);
        evenement.setTitre("Titre test");

        when(evenementRepository.findById(evenementId)).thenReturn(Optional.of(evenement));

        Evenement result = evenementService.getEvenementById(evenementId);

        assertNotNull(result);
        assertEquals(evenementId, ReflectionTestUtils.getField(result, "id"));
        assertEquals("Titre test", result.getTitre());
    }

    /**
     * Teste l'exception si l'événement est introuvable.
     */
    @Test
    void testGetEvenementById_NotFound_ThrowsException() {
        Long evenementId = 99L;

        when(evenementRepository.findById(evenementId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            evenementService.getEvenementById(evenementId);
        });

        assertEquals("Événement non trouvé", exception.getMessage());
    }

    /**
     * US43 Test1 - Création d'un événement
     * Création réussie avec tous les champs
     */
    @Test
    void creerEvenement_QuandDonneesValides_DoitSauvegarder() {
        // Arrange
        Evenement event = new Evenement();
        event.setTitre("Meetup");
        event.setDescription("Dev meeting");
        event.setDatePublication(LocalDateTime.now());

        when(evenementRepository.save(any())).thenReturn(event);

        // Act
        Evenement result = evenementService.creerEvenement(event);

        // Assert
        assertNotNull(result);
        verify(evenementRepository).save(event);
    }

    /**
     * US43 Test2 - Création d'un événement
     * Tentative de création sans titre
     */
    @Test
    void creerEvenement_QuandTitreManquant_DoitLeverException() {
        // Arrange
        Evenement event = new Evenement();
        event.setDescription("Desc");
        event.setDatePublication(LocalDateTime.now());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> evenementService.creerEvenement(event));
        assertEquals("Le titre est obligatoire", exception.getMessage());
        verifyNoInteractions(evenementRepository);
    }

    /**
     * US43 Test3 - Création d'un événement
     * Tentative de création sans description
     */
    @Test
    void creerEvenement_QuandDescriptionManquante_DoitLeverException() {
        // Arrange
        Evenement event = new Evenement();
        event.setTitre("Titre valide");
        event.setDatePublication(LocalDateTime.now());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> evenementService.creerEvenement(event));
        assertEquals("La description est obligatoire", exception.getMessage());
        verifyNoInteractions(evenementRepository);
    }

    /**
     * US43 Test4 - Création d'un événement
     * Tentative de création sans être connecté
     */
    @Test
    void creerEvenement_QuandDateManquante_DoitLeverException() {
        // Arrange
        Evenement event = new Evenement();
        event.setTitre("Titre");
        event.setDescription("Description");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> evenementService.creerEvenement(event));
        assertEquals("Date invalide", exception.getMessage());
        verifyNoInteractions(evenementRepository);
    }

    /**
     * US44 Test1 - Modifier événement
     * Tentative de modification par l'auteur
     */
    @Test
    void testModifierEvenement_AuteurSucces() {
        Long evenementId = 1L;

        Utilisateur auteur = new Utilisateur();
        ReflectionTestUtils.setField(auteur, "id", 100L);

        Evenement ancienEvenement = new Evenement();
        ReflectionTestUtils.setField(ancienEvenement, "id", evenementId);
        ancienEvenement.setAuteur(auteur);
        ancienEvenement.setTitre("Ancien titre");
        ancienEvenement.setDescription("Ancienne description");
        ancienEvenement.setVisibilite(VisibiliteEvenement.PRIVE);

        Evenement nouveau = new Evenement();
        nouveau.setTitre("Nouveau titre");
        nouveau.setDescription("Nouvelle description");
        nouveau.setContenu("Contenu modifié");
        nouveau.setVisibilite(VisibiliteEvenement.PUBLIC);

        when(evenementRepository.findById(evenementId)).thenReturn(Optional.of(ancienEvenement));
        when(evenementRepository.save(any(Evenement.class))).thenAnswer(i -> i.getArgument(0));

        Evenement resultat = evenementService.modifierEvenement(evenementId, nouveau, auteur);

        assertEquals("Nouveau titre", resultat.getTitre());
        assertEquals("Nouvelle description", resultat.getDescription());
        assertEquals("Contenu modifié", resultat.getContenu());
        assertEquals(VisibiliteEvenement.PUBLIC, resultat.getVisibilite());
    }

    /**
     * US44 Test2 - Modifier événement
     * Tentative de modification par un non-auteur
     */
    @Test
    void testModifierEvenement_NonAuteur_Exception() {
        Long evenementId = 1L;

        Utilisateur auteur = new Utilisateur();
        ReflectionTestUtils.setField(auteur, "id", 100L);

        Utilisateur intrus = new Utilisateur();
        ReflectionTestUtils.setField(intrus, "id", 200L);

        Evenement ancienEvenement = new Evenement();
        ReflectionTestUtils.setField(ancienEvenement, "id", evenementId);
        ancienEvenement.setAuteur(auteur);
        ancienEvenement.setTitre("Titre");
        ancienEvenement.setDescription("Description");
        ancienEvenement.setVisibilite(VisibiliteEvenement.PRIVE);

        Evenement nouveau = new Evenement();
        nouveau.setTitre("Titre modifié");
        nouveau.setDescription("Description modifiée");
        nouveau.setContenu("Contenu modifié");
        nouveau.setVisibilite(VisibiliteEvenement.PUBLIC);

        when(evenementRepository.findById(evenementId)).thenReturn(Optional.of(ancienEvenement));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            evenementService.modifierEvenement(evenementId, nouveau, intrus);
        });

        assertEquals("Seul l'auteur peut modifier l'événement", exception.getMessage());
    }

    /**
     * US44 Test3 - Modifier événement
     * Tentative de création sans titre
     */
    @Test
    void testModifierEvenement_TitreManquant_Exception() {
        Long evenementId = 1L;

        Utilisateur auteur = new Utilisateur();
        ReflectionTestUtils.setField(auteur, "id", 100L);

        Evenement ancienEvenement = new Evenement();
        ReflectionTestUtils.setField(ancienEvenement, "id", evenementId);
        ancienEvenement.setAuteur(auteur);
        ancienEvenement.setTitre("Titre existant");
        ancienEvenement.setDescription("Description existante");
        ancienEvenement.setVisibilite(VisibiliteEvenement.PRIVE);

        Evenement nouveau = new Evenement();
        nouveau.setTitre("  "); // titre vide ou blanc pour déclencher l'exception
        nouveau.setDescription("Description modifiée");
        nouveau.setContenu("Contenu modifié");
        nouveau.setVisibilite(VisibiliteEvenement.PUBLIC);

        when(evenementRepository.findById(evenementId)).thenReturn(Optional.of(ancienEvenement));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            evenementService.modifierEvenement(evenementId, nouveau, auteur);
        });

        assertEquals("Le titre est obligatoire", exception.getMessage());
    }

    /**
     * US44 Test4 - Modifier événement
     * Tentative de création sans description
     */
    @Test
    void testModifierEvenement_DescriptionManquante_Exception() {
        Long evenementId = 1L;

        Utilisateur auteur = new Utilisateur();
        ReflectionTestUtils.setField(auteur, "id", 100L);

        Evenement ancienEvenement = new Evenement();
        ReflectionTestUtils.setField(ancienEvenement, "id", evenementId);
        ancienEvenement.setAuteur(auteur);
        ancienEvenement.setTitre("Titre existant");
        ancienEvenement.setDescription("Description existante");
        ancienEvenement.setVisibilite(VisibiliteEvenement.PRIVE);

        Evenement nouveau = new Evenement();
        nouveau.setTitre("Titre modifié");
        nouveau.setDescription(null); // description manquante pour déclencher l'exception
        nouveau.setContenu("Contenu modifié");
        nouveau.setVisibilite(VisibiliteEvenement.PUBLIC);

        when(evenementRepository.findById(evenementId)).thenReturn(Optional.of(ancienEvenement));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            evenementService.modifierEvenement(evenementId, nouveau, auteur);
        });

        assertEquals("La description est obligatoire", exception.getMessage());
    }

    /**
     * US45 Test1 - Suppression d'un événement
     * Suppression par l'auteur
     */
    @Test
    void supprimerEvenement_QuandAuteurValide_DoitSupprimer() {
        // Arrange
        Long eventId = 1L;
        Utilisateur auteur = new Utilisateur();
        ReflectionTestUtils.setField(auteur, "id", 100L);

        Evenement event = new Evenement();
        event.setAuteur(auteur);
        when(evenementRepository.findById(eventId)).thenReturn(Optional.of(event));

        // Act
        evenementService.supprimerEvenement(eventId, auteur);

        // Assert
        verify(evenementRepository).deleteById(eventId);
    }

    /**
     * US45 Test2 - Suppression d'un événement
     * Tentative de suppression par non-auteur
     */
    @Test
    void supprimerEvenement_QuandNonAuteur_DoitLeverException() {
        // Arrange
        Long eventId = 1L;
        Utilisateur auteur = new Utilisateur();
        ReflectionTestUtils.setField(auteur, "id", 100L);

        Utilisateur intrus = new Utilisateur();
        ReflectionTestUtils.setField(intrus, "id", 200L);

        Evenement event = new Evenement();
        event.setAuteur(auteur);
        when(evenementRepository.findById(eventId)).thenReturn(Optional.of(event));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> evenementService.supprimerEvenement(eventId, intrus));
    }

    /**
     * US47 Test1 - Participer à un événement
     * Participation réussie à un événement
     */
    @Test
    void participerEvenement_QuandNouveauParticipant_DoitAjouter() {
        // Arrange
        Long eventId = 1L;
        Utilisateur participant = new Utilisateur();
        ReflectionTestUtils.setField(participant, "id", 100L);

        Evenement event = new Evenement();
        ReflectionTestUtils.setField(event, "id", eventId);

        when(evenementRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(evenementRepository.save(any())).thenAnswer(invocatiom -> invocatiom.getArgument(0));

        // Act
        Evenement result = evenementService.participerEvenement(eventId, participant);

        // Assert
        assertTrue(result.getParticipants().contains(participant));
        verify(evenementRepository).save(event);
    }

    /**
     * US47 Test2 - Participer à un événement
     * Tentative de participation déjà existante
     */
    @Test
    void participerEvenement_QuandDejaInscrit_DoitLeverException() {
        // Arrange
        Long eventId = 1L;
        Utilisateur participant = new Utilisateur();
        ReflectionTestUtils.setField(participant, "id", 100L);

        Evenement event = new Evenement();
        ReflectionTestUtils.setField(event, "id", eventId);
        event.addParticipant(participant);

        when(evenementRepository.findById(eventId)).thenReturn(Optional.of(event));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> evenementService.participerEvenement(eventId, participant));
        assertEquals("Vous êtes déjà inscrit à cet événement", exception.getMessage());
    }

    /**
     * US48 Test1 - Visualisation des participants à un événement
     * Visualiser les participants d'un événement existant
     */
    @Test
    void getParticipantsEvenement_QuandEvenementExistant_DoitRetournerListe() {
        // Arrange
        Long eventId = 1L;
        Evenement event = new Evenement();
        event.addParticipant(new Utilisateur("Test", "User", "test@example.com", "pass"));

        when(evenementRepository.findById(eventId)).thenReturn(Optional.of(event));

        // Act
        List<Utilisateur> result = evenementService.getParticipantsEvenement(eventId);

        // Assert
        assertEquals(1, result.size());
        verify(evenementRepository).findById(eventId);
    }

    /**
     * US48 Test2 - Visualisation des participants à un événement
     * Tentative d'accès à un événement inexistant
     */
    @Test
    void getParticipantsEvenement_QuandEvenementInexistant_DoitLeverException() {
        // Arrange
        Long invalidId = 999L;
        when(evenementRepository.findById(invalidId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> evenementService.getParticipantsEvenement(invalidId));
    }

}
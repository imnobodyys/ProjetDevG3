package utcapitole.miage.projetDevG3.Service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import utcapitole.miage.projetDevG3.Repository.EvenementRepository;
import utcapitole.miage.projetDevG3.model.*;

import java.time.LocalDateTime;
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
            () -> evenementService.creerEvenement(event)
        );
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
            () -> evenementService.creerEvenement(event)
        );
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
            () -> evenementService.creerEvenement(event)
        );
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


}
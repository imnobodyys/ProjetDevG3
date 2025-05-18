package utcapitole.miage.projetDevG3.Service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import utcapitole.miage.projetDevG3.Repository.EvenementRepository;
import utcapitole.miage.projetDevG3.model.Evenement;
import java.time.LocalDateTime;
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
}
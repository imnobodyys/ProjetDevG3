package utcapitole.miage.ProjetDevG3.ServiceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import utcapitole.miage.projetDevG3.Repository.UtilisateurRepository;
import utcapitole.miage.projetDevG3.Service.UtilisateurService;
import utcapitole.miage.projetDevG3.model.Utilisateur;


/**
 * Classe de test pour le service de gestion des utilisateurs.
 */
@ExtendWith(MockitoExtension.class)
public class UtilisateurServiceTest {
    @Mock
    private UtilisateurRepository utilisateurRepository;

     @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UtilisateurService utilisateurService;

    /**
     * US01 Test1 - Création réussie d'un nouveau profil
     * Teste la création d'un utilisateur avec un nouvel email.
     * Vérifie le cryptage du mot de passe et l'enregistrement en base.
     */
    @Test
    void creerUtilisateur_QuandNouvelEmail_DoitSauvegarderUtilisateur() {
        // Arrange
        Utilisateur user = new Utilisateur();
        user.setEmail("test@example.com");
        user.setMdp("rawPassword");
        
        when(utilisateurRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("rawPassword")).thenReturn("encryptedPassword");
        when(utilisateurRepository.save(any())).thenReturn(user);

        // Act
        Utilisateur result = utilisateurService.creerUtilisateur(user);

        // Assert
        assertNotNull(result);
        assertEquals("encryptedPassword", result.getMdp());
        assertNotNull(result.getDtInscription());
        verify(utilisateurRepository).save(user);
    }


    /**
     * US01 Test2 - Création avec un email existant
     * Teste la tentative de création avec un email existant.
     * Doit lever une exception avec le message approprié.
     */
    @Test
    void creerUtilisateur_QuandEmailExistant_DoitLeverException() {
        // Arrange
        String emailExistant = "existant@example.com";
        when(utilisateurRepository.findByEmail(emailExistant))
            .thenReturn(Optional.of(new Utilisateur()));

        Utilisateur user = new Utilisateur();
        user.setEmail(emailExistant);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> utilisateurService.creerUtilisateur(user)
        );
        
        assertEquals("Cet email est déjà utilisé !", exception.getMessage());
        verify(utilisateurRepository, never()).save(any());
    }



    /**
     * US01 Test3 - Initialiser automatiquement la date d’inscription avant la sauvegarde
     * Teste l'initialisation automatique de la date d'inscription.
     * Vérifie que la date est définie avant la persistance.
     */
    @Test
    void creerUtilisateur_DoitInitialiserDateInscriptionAvantSauvegarde() {
        // Arrange
        String nouveauEmail = "test@date.com";
        Utilisateur user = new Utilisateur();
        user.setEmail(nouveauEmail);

        when(utilisateurRepository.findByEmail(nouveauEmail))
            .thenReturn(Optional.empty());
        when(utilisateurRepository.save(any(Utilisateur.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Utilisateur result = utilisateurService.creerUtilisateur(user);

        // Assert
        verify(utilisateurRepository).save(user);
        assertNotNull(result.getDtInscription(), "La date d'inscription doit être initialisée");
        LocalDateTime maintenant = LocalDateTime.now();
        assertTrue(
            result.getDtInscription().isBefore(maintenant.plusSeconds(1)) &&
            result.getDtInscription().isAfter(maintenant.minusSeconds(1)),
            "La date d'inscription doit être approximativement maintenant"
        );
}
}

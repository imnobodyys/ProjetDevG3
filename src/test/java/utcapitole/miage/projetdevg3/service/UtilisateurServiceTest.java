package utcapitole.miage.projetdevg3.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.repository.UtilisateurRepository;
import utcapitole.miage.projetdevg3.service.UtilisateurService;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de test pour le service de gestion des utilisateurs.
 */
@ExtendWith(MockitoExtension.class)
class UtilisateurServiceTest {

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
                () -> utilisateurService.creerUtilisateur(user));

        assertEquals("Cet email est déjà utilisé !", exception.getMessage());
        verify(utilisateurRepository, never()).save(any());
    }

    /**
     * US01 Test3 - Initialiser automatiquement la date d'inscription avant la
     * sauvegarde
     * Teste l'initialisation automatique de la date d'inscription.
     * Vérifie que la date est définie avant la persistance.
     */
    @Test
    void creerUtilisateur_DoitInitialiserDateInscriptionAvantSauvegarde() {
        // Arrange
        String nouveauEmail = "test@date.com";
        Utilisateur user = new Utilisateur();
        user.setEmail(nouveauEmail);
        user.setMdp("password"); // Ajout nécessaire

        when(utilisateurRepository.findByEmail(nouveauEmail))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encrypted"); // Configuration manquante
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
                "La date d'inscription doit être approximativement maintenant");
    }

    /**
     * US03 Test1 - Modification du profil
     * Modifier son profil avec des données valides
     */
    @Test
    void modifierUtilisateur_QuandDonneesValides_DoitMettreAJourUtilisateur() {
        // Arrange
        Long userId = 1L;
        Utilisateur existingUser = new Utilisateur("Old", "User", "old@example.com", "oldPassword");
        existingUser.setId(userId);

        Utilisateur newDetails = new Utilisateur("New", "Name", "new@example.com", "newPassword");

        when(utilisateurRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(utilisateurRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(utilisateurRepository.save(any())).thenReturn(existingUser);

        // Act
        Utilisateur updated = utilisateurService.modifierUtilisateur(userId, newDetails);

        // Assert
        assertEquals("New", updated.getNom());
        assertEquals("Name", updated.getPrenom());
        assertEquals("new@example.com", updated.getEmail());
        assertEquals("encodedNewPassword", updated.getMdp());
        verify(utilisateurRepository).save(existingUser);
    }

    /**
     * US03 Test2 - Modification du profil
     * Tentative de modification avec un email déjà utilisé
     */
    @Test
    void modifierUtilisateur_QuandEmailExistant_DoitLeverException() {
        // Arrange
        Long userId = 1L;
        String existingEmail = "existing@example.com";

        Utilisateur currentUser = new Utilisateur("Current", "User", "current@example.com", "pass");
        currentUser.setId(userId);

        Utilisateur invalidUpdate = new Utilisateur("New", "Name", existingEmail, "pass");

        when(utilisateurRepository.findById(userId)).thenReturn(Optional.of(currentUser));
        when(utilisateurRepository.findByEmail(existingEmail)).thenReturn(Optional.of(new Utilisateur()));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> utilisateurService.modifierUtilisateur(userId, invalidUpdate));

        assertEquals("Cet email est déjà utilisé", exception.getMessage());
        verify(utilisateurRepository, never()).save(any());
    }

    /**
     * US04 Test1 - Suppression de profil
     * Suppression d'un utilisateur existant
     */
    @Test
    void supprimerUtilisateur_QuandIdValide_DoitAppelerDelete() {
        Long userId = 1L;
        when(utilisateurRepository.existsById(userId)).thenReturn(true);

        utilisateurService.supprimerUtilisateur(userId);

        verify(utilisateurRepository).deleteById(userId);
    }

    /**
     * US04 Test2 - Suppression de profil
     * Tentative de suppression d'un utilisateur inexistant
     */
    @Test
    void supprimerUtilisateur_QuandIdInvalide_DoitLeverException() {
        Long invalidId = 999L;
        when(utilisateurRepository.existsById(invalidId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> utilisateurService.supprimerUtilisateur(invalidId));
    }

    @Test
    void testRechercheKeywordFound() {
        Utilisateur u = new Utilisateur("Alice", "Dupont", "alice@test.com", "1234");
        when(utilisateurRepository.searchByKeyword("ali")).thenReturn(List.of(u));

        List<Utilisateur> result = utilisateurService.rechercher("ali");

        assertEquals(1, result.size());
        assertEquals("Alice", result.get(0).getNom());
        verify(utilisateurRepository).searchByKeyword("ali");
    }

    /**
     * test pour rechercher no result
     */
    @Test
    void testRechercheNoResult() {
        when(utilisateurRepository.searchByKeyword("zzz")).thenReturn(Collections.emptyList());

        List<Utilisateur> result = utilisateurService.rechercher("zzz");

        assertTrue(result.isEmpty());
    }

    /**
     * Test - Authentification réussie
     * Vérifie que l'utilisateur est retourné si les identifiants sont corrects.
     */
    @Test
    void authentifier_QuandIdentifiantsCorrects_DoitRetournerUtilisateur() {
        Utilisateur user = new Utilisateur("Jean", "Dupont", "jean@example.com", "encodedPass");
        when(utilisateurRepository.findByEmail("jean@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("monMotDePasse", "encodedPass")).thenReturn(true);

        Utilisateur result = utilisateurService.authentifier("jean@example.com", "monMotDePasse");

        assertNotNull(result);
        assertEquals("jean@example.com", result.getEmail());
    }

    /**
     * Test - Authentification échouée
     * Vérifie que null est retourné si le mot de passe est incorrect.
     */
    @Test
    void authentifier_QuandMotDePasseIncorrect_DoitRetournerNull() {
        Utilisateur user = new Utilisateur("Jean", "Dupont", "jean@example.com", "encodedPass");
        when(utilisateurRepository.findByEmail("jean@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("faux", "encodedPass")).thenReturn(false);

        Utilisateur result = utilisateurService.authentifier("jean@example.com", "faux");

        assertNull(result);
    }

    /**
     * Test - Authentification utilisateur inexistant
     * Vérifie que null est retourné si l'utilisateur n'existe pas.
     */
    @Test
    void authentifier_QuandEmailInconnu_DoitRetournerNull() {
        when(utilisateurRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        Utilisateur result = utilisateurService.authentifier("notfound@example.com", "motdepasse");

        assertNull(result);
    }

    /**
     * Test - Recherche d'utilisateur par email
     * Vérifie que l'utilisateur est retourné si l'email existe.
     */
    @Test
    void findByEmail_QuandEmailExiste_DoitRetournerUtilisateur() {
        Utilisateur user = new Utilisateur();
        user.setEmail("contact@exemple.com");

        when(utilisateurRepository.findByEmail("contact@exemple.com"))
                .thenReturn(Optional.of(user));

        Utilisateur result = utilisateurService.findByEmail("contact@exemple.com");

        assertEquals("contact@exemple.com", result.getEmail());
    }

    /**
     * Test - Email non trouvé
     * Doit lever UsernameNotFoundException si l'utilisateur n'existe pas.
     */
    @Test
    void findByEmail_QuandEmailInexistant_DoitLeverException() {
        when(utilisateurRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            utilisateurService.findByEmail("unknown@example.com");
        });
    }

    /**
     * Test - Trouver utilisateur par ID
     * Vérifie que l'utilisateur est retourné si l'ID existe.
     */
    @Test
    void trouverParId_QuandIdValide_DoitRetournerUtilisateur() {
        Utilisateur user = new Utilisateur();
        user.setId(42L);

        when(utilisateurRepository.findById(42L)).thenReturn(Optional.of(user));

        Utilisateur result = utilisateurService.trouverParId(42L);

        assertNotNull(result);
        assertEquals(42L, result.getId());
    }

    /**
     * Test - ID inconnu
     * Vérifie que null est retourné si aucun utilisateur n'est trouvé.
     */
    @Test
    void trouverParId_QuandIdInvalide_DoitRetournerNull() {
        when(utilisateurRepository.findById(999L)).thenReturn(Optional.empty());

        Utilisateur result = utilisateurService.trouverParId(999L);

        assertNull(result);
    }

}
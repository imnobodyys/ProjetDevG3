package utcapitole.miage.projetDevG3.Servicetest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utcapitole.miage.projetDevG3.Repository.UtilisateurRepository;
import utcapitole.miage.projetDevG3.Service.UtilisateurService;
import utcapitole.miage.projetDevG3.model.Utilisateur;

import java.util.List;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UtilisateurServiceTest {

    private UtilisateurRepository utilisateurRepository;
    private UtilisateurService utilisateurService;

    @BeforeEach
    void setUp() {
        utilisateurRepository = mock(UtilisateurRepository.class);
        utilisateurService = new UtilisateurService(utilisateurRepository);
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

    @Test
    void testRechercheNoResult() {
        when(utilisateurRepository.searchByKeyword("zzz")).thenReturn(Collections.emptyList());

        List<Utilisateur> result = utilisateurService.rechercher("zzz");

        assertTrue(result.isEmpty());
    }
}

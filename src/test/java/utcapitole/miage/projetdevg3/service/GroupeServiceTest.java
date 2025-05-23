package utcapitole.miage.projetdevg3.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import utcapitole.miage.projetdevg3.model.Groupe;
import utcapitole.miage.projetdevg3.model.MembreGroupe;
import utcapitole.miage.projetdevg3.model.StatutMembre;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.repository.GroupeRepository;
import utcapitole.miage.projetdevg3.repository.MembreGroupeRepository;
import utcapitole.miage.projetdevg3.service.GroupeService;

class GroupeServiceTest {
    @InjectMocks
    private GroupeService groupeService;

    @Mock
    private GroupeRepository groupeRepository;

    @Mock
    private MembreGroupeRepository membreGroupeRepository;

    @BeforeEach
    void setup() {
        groupeRepository = mock(GroupeRepository.class);
        membreGroupeRepository = mock(MembreGroupeRepository.class);

        groupeService = new GroupeService(groupeRepository, membreGroupeRepository);
    }

    @Test
    void testCreerGroupe() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(1L);
        utilisateur.setNom("Alice");

        Groupe groupeEnregistre = new Groupe();
        groupeEnregistre.setId(100L);
        groupeEnregistre.setNom("Java Fans");

        // Simule la sauvegarde du groupe
        when(groupeRepository.save(any(Groupe.class))).thenReturn(groupeEnregistre);

        Groupe resultat = groupeService.creerGroupe("Java Fans", "Groupe pour les fans de Java", utilisateur);

        assertNotNull(resultat);
        assertEquals("Java Fans", resultat.getNom());
        verify(groupeRepository).save(any(Groupe.class));
        verify(membreGroupeRepository).save(any(MembreGroupe.class));
    }
    @Test
    void testDemanderAdhesion_nouvelleDemande() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(1L);

        Groupe groupe = new Groupe();
        groupe.setId(10L);

        when(groupeRepository.findById(10L)).thenReturn(Optional.of(groupe));
        when(membreGroupeRepository.findByMembreUtilisateur(utilisateur)).thenReturn(List.of());

        groupeService.demanderAdhesion(10L, utilisateur);

        verify(membreGroupeRepository, times(1)).save(any(MembreGroupe.class));
    }

    @Test
    void testDemanderAdhesion_dejaMembreOuEnAttente() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(1L);

        Groupe groupe = new Groupe();
        groupe.setId(10L);

        MembreGroupe membreExistant = new MembreGroupe(utilisateur, groupe);
        membreExistant.setGroupe(groupe);
        membreExistant.setMembreUtilisateur(utilisateur);

        when(groupeRepository.findById(10L)).thenReturn(Optional.of(groupe));
        when(membreGroupeRepository.findByMembreUtilisateur(utilisateur)).thenReturn(List.of(membreExistant));

        groupeService.demanderAdhesion(10L, utilisateur);

        verify(membreGroupeRepository, never()).save(any());
    }

    @Test
    void testChangerStatutMembre() {
        MembreGroupe membre = new MembreGroupe(null, null);
        membre.setId(5L);
        membre.setStatut(StatutMembre.EN_ATTENTE);

        when(membreGroupeRepository.findById(5L)).thenReturn(Optional.of(membre));

        groupeService.changerStatutMembre(5L, StatutMembre.ACCEPTE);

        assertEquals(StatutMembre.ACCEPTE, membre.getStatut());
        verify(membreGroupeRepository).save(membre);
    }
    @Test
    void testCreerGroupe_nomDejaUtilise() {
        when(groupeRepository.existsByNomIgnoreCase("NomExist")).thenReturn(true);

        Utilisateur createur = new Utilisateur();
        createur.setId(1L);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> groupeService.creerGroupe("NomExist", "desc", createur));
        assertEquals("Ce nom de groupe est déjà utilisé.", ex.getMessage());

        verify(groupeRepository, never()).save(any());
    }
     @Test
    void testGetGroupeById_existe() {
        Groupe groupe = new Groupe();
        groupe.setId(123L);

        when(groupeRepository.findById(123L)).thenReturn(Optional.of(groupe));
        Groupe g = groupeService.getGroupeById(123L);

        assertEquals(123L, g.getId());
    }

    @Test
    void testGetGroupeById_inexistant() {
        when(groupeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> groupeService.getGroupeById(999L));
    }

    @Test
    void testGetGroupesDisponiblesPour_utilisateurAvecGroupes() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(1L);

        Groupe g1 = new Groupe();
        g1.setId(10L);
        Utilisateur createurG1 = new Utilisateur();
        createurG1.setId(2L);
        g1.setCreateur(createurG1);

        Groupe g2 = new Groupe();
        g2.setId(20L);
        g2.setCreateur(utilisateur); // créé par l'utilisateur, doit être filtré

        when(membreGroupeRepository.findByMembreUtilisateur(utilisateur)).thenReturn(List.of(
            new MembreGroupe(utilisateur, g1)
        ));
        when(groupeRepository.findAll()).thenReturn(List.of(g1, g2));

        List<Groupe> result = groupeService.getGroupesDisponiblesPour(utilisateur);

        assertEquals(0, result.size(), "Utilisateur a déjà rejoint g1, g2 est exclu car créé par lui");
    }

    @Test
    void testAnnulerDemande_supprimeEnAttente() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(1L);

        Groupe groupe = new Groupe();
        groupe.setId(10L);

        MembreGroupe demandeEnAttente = new MembreGroupe(utilisateur, groupe);
        demandeEnAttente.setStatut(StatutMembre.EN_ATTENTE);

        MembreGroupe demandeAcceptee = new MembreGroupe(utilisateur, groupe);
        demandeAcceptee.setStatut(StatutMembre.ACCEPTE);

        when(groupeRepository.findById(10L)).thenReturn(Optional.of(groupe));
        when(membreGroupeRepository.findByMembreUtilisateur(utilisateur)).thenReturn(List.of(demandeEnAttente, demandeAcceptee));

        groupeService.annulerDemande(10L, utilisateur);

        verify(membreGroupeRepository).deleteAll(List.of(demandeEnAttente));
    }

    @Test
    void testChangerStatutMembre_membreExiste() {
        MembreGroupe membre = new MembreGroupe();
        membre.setId(5L);
        membre.setStatut(StatutMembre.EN_ATTENTE);

        when(membreGroupeRepository.findById(5L)).thenReturn(Optional.of(membre));

        groupeService.changerStatutMembre(5L, StatutMembre.ACCEPTE);

        assertEquals(StatutMembre.ACCEPTE, membre.getStatut());
        verify(membreGroupeRepository).save(membre);
    }

    @Test
    void testSupprimerGroupeSiCreateur_succes() {
        Utilisateur createur = new Utilisateur();
        createur.setId(1L);

        Groupe groupe = new Groupe();
        groupe.setId(10L);
        groupe.setCreateur(createur);

        when(groupeRepository.findById(10L)).thenReturn(Optional.of(groupe));

        groupeService.supprimerGroupeSiCreateur(10L, createur);

        verify(groupeRepository).deleteById(10L);
    }

    @Test
    void testSupprimerGroupeSiCreateur_nonCreateur() {
        Utilisateur createur = new Utilisateur();
        createur.setId(1L);

        Utilisateur autreUtilisateur = new Utilisateur();
        autreUtilisateur.setId(2L);

        Groupe groupe = new Groupe();
        groupe.setId(10L);
        groupe.setCreateur(createur);

        when(groupeRepository.findById(10L)).thenReturn(Optional.of(groupe));

        SecurityException ex = assertThrows(SecurityException.class,
            () -> groupeService.supprimerGroupeSiCreateur(10L, autreUtilisateur));
        assertEquals("Seul le créateur peut supprimer ce groupe", ex.getMessage());

        verify(groupeRepository, never()).deleteById(any());
    }

    @Test
    void testGetGroupesAvecDemandeEnAttente() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(1L);

        Groupe groupe1 = new Groupe();
        groupe1.setId(11L);

        MembreGroupe demande1 = new MembreGroupe(utilisateur, groupe1);
        demande1.setStatut(StatutMembre.EN_ATTENTE);

        when(membreGroupeRepository.findByMembreUtilisateurAndStatut(utilisateur, StatutMembre.EN_ATTENTE))
            .thenReturn(List.of(demande1));

        List<Groupe> groupes = groupeService.getGroupesAvecDemandeEnAttente(utilisateur);

        assertEquals(1, groupes.size());
        assertEquals(groupe1, groupes.get(0));
    }

    @Test
    void testExclureMembre_verifieDeleteById() {
        groupeService.exclureMembre(123L);
        verify(membreGroupeRepository).deleteById(123L);
    }

    @Test
    void testGetStatutPourUtilisateur_retourneStatut() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(1L);

        Groupe groupe = new Groupe();
        groupe.setId(10L);

        MembreGroupe membre = new MembreGroupe(utilisateur, groupe);
        membre.setStatut(StatutMembre.ACCEPTE);

        when(membreGroupeRepository.findByMembreUtilisateur(utilisateur)).thenReturn(List.of(membre));

        StatutMembre statut = groupeService.getStatutPourUtilisateur(groupe, utilisateur);
        assertEquals(StatutMembre.ACCEPTE, statut);
    }

    @Test
    void testGetStatutPourUtilisateur_retourneNullSiPasMembre() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(1L);

        Groupe groupe = new Groupe();
        groupe.setId(10L);

        when(membreGroupeRepository.findByMembreUtilisateur(utilisateur)).thenReturn(List.of());

        StatutMembre statut = groupeService.getStatutPourUtilisateur(groupe, utilisateur);
        assertNull(statut);
    }

    @Test
    void testGetGroupesDisponiblesPourUtilisateur_filtrageParStatut() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(1L);

        Groupe groupe1 = new Groupe();
        groupe1.setId(1L);

        Groupe groupe2 = new Groupe();
        groupe2.setId(2L);

        when(groupeRepository.findAll()).thenReturn(List.of(groupe1, groupe2));


        MembreGroupe membre1 = new MembreGroupe(utilisateur, groupe1);
        membre1.setStatut(StatutMembre.REFUSE);

        MembreGroupe membre2 = new MembreGroupe(utilisateur, groupe2);
        membre2.setStatut(StatutMembre.ACCEPTE);

        when(membreGroupeRepository.findByMembreUtilisateur(utilisateur)).thenReturn(List.of(membre1, membre2));

        List<Groupe> disponibles = groupeService.getGroupesDisponiblesPourUtilisateur(utilisateur);

        assertEquals(1, disponibles.size());
        assertTrue(disponibles.contains(groupe1));
        assertFalse(disponibles.contains(groupe2));
    }
}

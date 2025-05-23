package utcapitole.miage.projetdevg3.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import utcapitole.miage.projetdevg3.model.Groupe;
import utcapitole.miage.projetdevg3.model.MembreGroupe;
import utcapitole.miage.projetdevg3.model.StatutMembre;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.repository.MembreGroupeRepository;

public class MembreGroupeServiceTest {
    @Mock
    private MembreGroupeRepository membreGroupeRepository;

    @Mock
    private GroupeService groupeService;

    @InjectMocks
    private MembreGroupeService membreGroupeService;

    private Groupe groupe;
    private Utilisateur utilisateur;
    private MembreGroupe membre;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        utilisateur = new Utilisateur();
        utilisateur.setId(1L);
        utilisateur.setNom("Doe");

        groupe = new Groupe();
        groupe.setId(10L);
        groupe.setNom("Groupe Test");
        groupe.setCreateur(new Utilisateur());
        groupe.getCreateur().setId(2L);  // autre utilisateur

        membre = new MembreGroupe();
        membre.setId(100L);
        membre.setGroupe(groupe);
        membre.setMembreUtilisateur(utilisateur);
        membre.setStatut(StatutMembre.EN_ATTENTE);
    }

    @Test
    void testGetMembresParGroupeEtStatut() {
        List<MembreGroupe> membres = List.of(membre);
        when(membreGroupeRepository.findByGroupeAndStatut(groupe, StatutMembre.EN_ATTENTE)).thenReturn(membres);

        List<MembreGroupe> result = membreGroupeService.getMembresParGroupeEtStatut(groupe, StatutMembre.EN_ATTENTE);
        assertEquals(1, result.size());
        assertEquals(membre, result.get(0));
    }

    @Test
    void testAccepterMembre_Succes() {
        when(membreGroupeRepository.findById(membre.getId())).thenReturn(Optional.of(membre));
        when(membreGroupeRepository.save(any())).thenReturn(membre);

        membreGroupeService.accepterMembre(membre.getId());

        assertEquals(StatutMembre.ACCEPTE, membre.getStatut());
        verify(membreGroupeRepository).save(membre);
    }

    @Test
    void testAccepterMembre_MembreNonTrouve() {
        when(membreGroupeRepository.findById(membre.getId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            membreGroupeService.accepterMembre(membre.getId());
        });

        assertEquals("Membre non trouvé", exception.getMessage());
        verify(membreGroupeRepository, never()).save(any());
    }

    @Test
    void testRefuserMembre_Succes() {
        when(membreGroupeRepository.findById(membre.getId())).thenReturn(Optional.of(membre));
        when(membreGroupeRepository.save(any())).thenReturn(membre);

        membreGroupeService.refuserMembre(membre.getId());

        assertEquals(StatutMembre.REFUSE, membre.getStatut());
        verify(membreGroupeRepository).save(membre);
    }

    @Test
    void testExclureMembre_Succes() {
        when(membreGroupeRepository.findById(membre.getId())).thenReturn(Optional.of(membre));

        membreGroupeService.exclureMembre(membre.getId());

        verify(membreGroupeRepository).delete(membre);
    }

    @Test
    void testExclureMembre_MembreNonTrouve() {
        when(membreGroupeRepository.findById(membre.getId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            membreGroupeService.exclureMembre(membre.getId());
        });

        assertEquals("Membre non trouvé", exception.getMessage());
        verify(membreGroupeRepository, never()).delete(any());
    }

    @Test
    void testGetGroupesDisponiblesPourUtilisateur() {
        Groupe groupe2 = new Groupe();
        groupe2.setId(20L);
        groupe2.setNom("Groupe 2");
        groupe2.setCreateur(new Utilisateur());
        groupe2.getCreateur().setId(3L);

        // List de groupes retournée par groupeService
        List<Groupe> groupes = List.of(groupe, groupe2);

        when(groupeService.getTousLesGroupes()).thenReturn(groupes);

        // Membre déjà dans groupe1
        when(membreGroupeRepository.findByGroupeAndMembreUtilisateur(groupe, utilisateur)).thenReturn(Optional.of(membre));
        // Pas membre dans groupe2
        when(membreGroupeRepository.findByGroupeAndMembreUtilisateur(groupe2, utilisateur)).thenReturn(Optional.empty());

        List<Groupe> result = membreGroupeService.getGroupesDisponiblesPourUtilisateur(utilisateur);

        // groupe (id=10) est exclu car utilisateur est membre
        // groupe2 est retenu car utilisateur n'est ni créateur ni membre
        assertEquals(1, result.size());
        assertEquals(groupe2.getId(), result.get(0).getId());
    }

    @Test
    void testGetStatutPourUtilisateur_Present() {
        when(membreGroupeRepository.findByGroupeAndMembreUtilisateur(groupe, utilisateur))
            .thenReturn(Optional.of(membre));

        StatutMembre statut = membreGroupeService.getStatutPourUtilisateur(groupe, utilisateur);
        assertEquals(StatutMembre.EN_ATTENTE, statut);
    }

    @Test
    void testGetStatutPourUtilisateur_Absent() {
        when(membreGroupeRepository.findByGroupeAndMembreUtilisateur(groupe, utilisateur))
            .thenReturn(Optional.empty());

        StatutMembre statut = membreGroupeService.getStatutPourUtilisateur(groupe, utilisateur);
        assertNull(statut);
    }

}

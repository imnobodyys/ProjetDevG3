package utcapitole.miage.projetdevg3.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

class GroupeServiceTest {
    private GroupeService groupeService;
    private GroupeRepository groupeRepository;
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

    /** US18 */
    @Test
    void testDemanderAdhesion_nouvelleDemande() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(1L);

        Groupe groupe = new Groupe();
        groupe.setId(10L);

        when(groupeRepository.findById(10L)).thenReturn(Optional.of(groupe));
        when(membreGroupeRepository.findByMembre(utilisateur)).thenReturn(List.of());

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
        membreExistant.setMembre(utilisateur);

        when(groupeRepository.findById(10L)).thenReturn(Optional.of(groupe));
        when(membreGroupeRepository.findByMembre(utilisateur)).thenReturn(List.of(membreExistant));

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

}

package utcapitole.miage.projetdevg3.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MembreGroupeTest {
    private MembreGroupe membreGroupe;
    private Utilisateur utilisateur;
    private Groupe groupe;

    @BeforeEach
    public void setUp() {
        utilisateur = new Utilisateur(); // Suppose que la classe Utilisateur existe
        groupe = new Groupe();
        membreGroupe = new MembreGroupe(utilisateur, groupe);
    }

    @Test
    public void testConstructorSetsStatutToEnAttente() {
        assertEquals(StatutMembre.EN_ATTENTE, membreGroupe.getStatut());
    }

    @Test
    public void testGettersAndSetters() {
        membreGroupe.setId(1L);
        membreGroupe.setMembreUtilisateur(utilisateur);
        membreGroupe.setGroupe(groupe);
        membreGroupe.setStatut(StatutMembre.ACCEPTE);

        assertEquals(1L, membreGroupe.getId());
        assertEquals(utilisateur, membreGroupe.getMembreUtilisateur());
        assertEquals(groupe, membreGroupe.getGroupe());
        assertEquals(StatutMembre.ACCEPTE, membreGroupe.getStatut());
    }

    @Test
    public void testSetGroupe() {
        Groupe newGroupe = new Groupe();
        membreGroupe.setGroupe(newGroupe);
        assertEquals(newGroupe, membreGroupe.getGroupe());
    }

    @Test
    public void testSetMembre() {
        Utilisateur newUtilisateur = new Utilisateur();
        membreGroupe.setMembreUtilisateur(newUtilisateur);
        assertEquals(newUtilisateur, membreGroupe.getMembreUtilisateur());
    }

}

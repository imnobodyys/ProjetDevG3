package utcapitole.miage.projetdevg3.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DemandeAmiTest {
     private DemandeAmi demandeAmi;
    private Utilisateur expediteur;
    private Utilisateur destinataire;
    private LocalDateTime now;

    @BeforeEach
    public void setup() {
        demandeAmi = new DemandeAmi();
        expediteur = new Utilisateur(); // Assure-toi que Utilisateur a un constructeur par défaut
        destinataire = new Utilisateur();
        now = LocalDateTime.now();
    }

    @Test
    public void testSetAndGetId() {
        demandeAmi.setId(1L);
        assertEquals(1L, demandeAmi.getId());
    }

    @Test
    public void testSetAndGetDtEnvoi() {
        demandeAmi.setDtEnvoi(now);
        assertEquals(now, demandeAmi.getDtEnvoi());
    }

    @Test
    public void testSetAndGetStatut() {
        demandeAmi.setStatut(StatutDemande.EN_ATTENTE);
        assertEquals(StatutDemande.EN_ATTENTE, demandeAmi.getStatut());

        demandeAmi.setStatut(StatutDemande.ACCEPTE);
        assertEquals(StatutDemande.ACCEPTE, demandeAmi.getStatut());

        demandeAmi.setStatut(StatutDemande.REFUSE);
        assertEquals(StatutDemande.REFUSE, demandeAmi.getStatut());
    }

    @Test
    public void testSetAndGetExpediteurAmi() {
        demandeAmi.setExpediteurAmi(expediteur);
        assertEquals(expediteur, demandeAmi.getExpediteurAmi());
    }

    @Test
    public void testSetAndGetDestinataireAmi() {
        demandeAmi.setDestinataireAmi(destinataire);
        assertEquals(destinataire, demandeAmi.getDestinataireAmi());
    }

}

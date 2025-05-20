package utcapitole.miage.projetDevG3.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import utcapitole.miage.projetDevG3.Controller.GroupeController;
import utcapitole.miage.projetDevG3.Service.GroupeService;
import utcapitole.miage.projetDevG3.Service.MembreGroupeService;
import utcapitole.miage.projetDevG3.Service.UtilisateurService;
import utcapitole.miage.projetDevG3.model.Utilisateur;

@WebMvcTest(GroupeController.class)
@AutoConfigureMockMvc(addFilters = false) // ⬅ DÉSACTIVE la sécurité pour les tests
public class GroupeControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GroupeService groupeService;

    @MockBean
    private MembreGroupeService membreGroupeService;
    @MockBean
    private UtilisateurService utilisateurService;

    // US17 - Création de groupe
    @Test
    void testCreerGroupe() throws Exception {
        Utilisateur user = new Utilisateur("Alice", "Dupont", "alice@example.com", "password");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("utilisateurConnecte", user);

        mockMvc.perform(post("/groupes/creer")
                .param("nom", "Groupe Test")
                .param("description", "Description test")
                .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/groupes/liste"));

        verify(groupeService).creerGroupe("Groupe Test", "Description test", user);
    }

    // US18 - Demande d’adhésion à un groupe
    @Test
    void testDemanderAdhesion() throws Exception {
        Utilisateur user = new Utilisateur("Bob", "Martin", "bob@example.com", "1234");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("utilisateurConnecte", user);

        mockMvc.perform(post("/groupes/rejoindre")
                .param("idGroupe", "1")
                .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/groupes/liste"));

        verify(groupeService).demanderAdhesion(1L, user);
    }

    // US19 - Modifier statut d’un membre
    @Test
    void testModifierStatutAccepter() throws Exception {
        mockMvc.perform(post("/groupes/admin/modifierStatut")
                .param("idMembre", "10")
                .param("idGroupe", "1")
                .param("action", "accepter"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/groupes/admin/demandes?idGroupe=1"));

        verify(membreGroupeService).accepterMembre(10L);
    }

    @Test
    void testModifierStatutRefuser() throws Exception {
        mockMvc.perform(post("/groupes/admin/modifierStatut")
                .param("idMembre", "11")
                .param("idGroupe", "1")
                .param("action", "refuser"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/groupes/admin/demandes?idGroupe=1"));

        verify(membreGroupeService).refuserMembre(11L);
    }

    @Test
    void testModifierStatutExclure() throws Exception {
        mockMvc.perform(post("/groupes/admin/modifierStatut")
                .param("idMembre", "12")
                .param("idGroupe", "1")
                .param("action", "exclure"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/groupes/admin/demandes?idGroupe=1"));

        verify(membreGroupeService).exclureMembre(12L);
    }

}

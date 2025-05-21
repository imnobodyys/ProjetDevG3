package utcapitole.miage.projetdevg3.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import org.springframework.test.web.servlet.MockMvc;

import utcapitole.miage.projetdevg3.service.GroupeService;
import utcapitole.miage.projetdevg3.service.MembreGroupeService;
import utcapitole.miage.projetdevg3.service.UtilisateurService;
import utcapitole.miage.projetdevg3.controller.GroupeController;
import utcapitole.miage.projetdevg3.model.Groupe;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.repository.UtilisateurRepository;

@WebMvcTest(GroupeController.class)
@AutoConfigureMockMvc
public class GroupeControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GroupeService groupeService;

    @MockBean
    private MembreGroupeService membreGroupeService;
    @MockBean
    private UtilisateurService utilisateurService;

    @MockBean
    private UtilisateurRepository utilisateurRepository;


    // US17 - Création de groupe
    @Test
    @WithMockUser(username = "alice@example.com", roles = "USER")
    void testCreerGroupe() throws Exception {
        Utilisateur user = new Utilisateur("Alice", "Dupont", "alice@example.com", "password");
        
        when(utilisateurService.findByEmail("alice@example.com"))
                 .thenReturn(user);

        mockMvc.perform(post("/groupes/creer")
                .param("nom", "Groupe Test")
                .param("description", "Description test")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/groupes/liste"));

        verify(groupeService).creerGroupe("Groupe Test", "Description test", user);
    }

    // US18 - Demande d’adhésion à un groupe
    @Test
    @WithMockUser(username = "bob@example.com", roles = "USER") //
    void testDemanderAdhesion() throws Exception {
        Utilisateur user = new Utilisateur("Bob", "Martin", "bob@example.com", "1234");
         when(utilisateurService.findByEmail("bob@example.com")).thenReturn(user);
       
        mockMvc.perform(post("/groupes/rejoindre")
                .param("idGroupe", "1")
                 .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/groupes/liste"));

        verify(groupeService).demanderAdhesion(1L, user);
    }

    // US19 - Modifier statut d’un membre
    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void testModifierStatutAccepter() throws Exception {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(100L);
        utilisateur.setEmail("admin@example.com");

        Groupe groupe = new Groupe();
        groupe.setId(1L);
        groupe.setCreateur(utilisateur);

        when(utilisateurService.findByEmail("admin@example.com")).thenReturn(utilisateur);
        when(groupeService.getGroupeById(1L)).thenReturn(groupe);

        mockMvc.perform(post("/groupes/admin/modifierStatut")
                .param("idMembre", "10")
                .param("idGroupe", "1")
                .param("action", "accepter")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/groupes/admin/demandes?idGroupe=1"));

        verify(membreGroupeService).accepterMembre(10L);
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void testModifierStatutRefuser() throws Exception {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(100L);
        utilisateur.setEmail("admin@example.com");

        Groupe groupe = new Groupe();
        groupe.setId(1L);
        groupe.setCreateur(utilisateur);

        when(utilisateurService.findByEmail("admin@example.com")).thenReturn(utilisateur);
        when(groupeService.getGroupeById(1L)).thenReturn(groupe);
        mockMvc.perform(post("/groupes/admin/modifierStatut")
                .param("idMembre", "11")
                .param("idGroupe", "1")
                .param("action", "refuser")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/groupes/admin/demandes?idGroupe=1"));

        verify(membreGroupeService).refuserMembre(11L);
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void testModifierStatutExclure() throws Exception {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(100L);
        utilisateur.setEmail("admin@example.com");

        Groupe groupe = new Groupe();
        groupe.setId(1L);
        groupe.setCreateur(utilisateur);

        when(utilisateurService.findByEmail("admin@example.com")).thenReturn(utilisateur);
        when(groupeService.getGroupeById(1L)).thenReturn(groupe);

        mockMvc.perform(post("/groupes/admin/modifierStatut")
                .param("idMembre", "12")
                .param("idGroupe", "1")
                .param("action", "exclure")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/groupes/admin/demandes?idGroupe=1"));

        verify(membreGroupeService).exclureMembre(12L);
    }
     @Test
    @WithMockUser(username = "testuser@example.com")
    public void testRejoindreGroupe_Success() throws Exception {
        Long groupeId = 1L;

        Utilisateur utilisateurMock = new Utilisateur();
        utilisateurMock.setId(42L);
        utilisateurMock.setEmail("testuser@example.com");

        
        when(utilisateurService.findByEmail("testuser@example.com")).thenReturn(utilisateurMock);

        mockMvc.perform(post("/groupes/rejoindre")
                .param("idGroupe", groupeId.toString())
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/groupes/liste"));

        // Vérifier que la méthode demanderAdhesion a bien été appelée
        verify(groupeService, times(1)).demanderAdhesion(groupeId, utilisateurMock);
    }

    @Test
    public void testRejoindreGroupe_Unauthenticated() throws Exception {
        Long groupeId = 1L;

        mockMvc.perform(post("/groupes/rejoindre")
                .param("idGroupe", groupeId.toString())
                .with(csrf()))
                .andExpect(status().isUnauthorized());
        
        verifyNoInteractions(groupeService);
    }

}

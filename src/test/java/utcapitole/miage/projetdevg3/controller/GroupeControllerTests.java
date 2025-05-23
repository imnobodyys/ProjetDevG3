package utcapitole.miage.projetdevg3.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import utcapitole.miage.projetdevg3.service.GroupeService;
import utcapitole.miage.projetdevg3.service.MembreGroupeService;
import utcapitole.miage.projetdevg3.service.UtilisateurService;

import utcapitole.miage.projetdevg3.model.Groupe;
import utcapitole.miage.projetdevg3.model.MembreGroupe;
import utcapitole.miage.projetdevg3.model.StatutMembre;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.repository.UtilisateurRepository;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;




import java.util.*;



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

    @Test
    public void testRejoindreGroupe_Unauthenticated() throws Exception {
        Long groupeId = 1L;

        mockMvc.perform(post("/groupes/rejoindre")
                .param("idGroupe", groupeId.toString())
                .with(csrf()))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(groupeService);
    }
    
    @Test
    @WithMockUser(username = "alice@example.com")
    void testAfficherFormulaire_Authenticated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/groupes/creer"))
                .andExpect(status().isOk())
                .andExpect(view().name("formulaireGroupe"))
                .andExpect(model().attributeExists("groupe"));
    }


    
    @Test
    @WithMockUser(username = "alice@example.com")
    void testCreerGroupe_Valid() throws Exception {
        Utilisateur user = new Utilisateur("Alice", "Dupont", "alice@example.com", "password");
        when(utilisateurService.findByEmail("alice@example.com")).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/groupes/creer")
                .param("nom", "Groupe Test")
                .param("description", "Description test")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/groupes/liste"))
                .andExpect(flash().attribute("message", "Groupe créé avec succès."));

        verify(groupeService).creerGroupe("Groupe Test", "Description test", user);
    }

    
    
    
    @Test
    @WithMockUser(username = "alice@example.com")
    void testCreerGroupe_ServiceThrowsException() throws Exception {
        Utilisateur user = new Utilisateur("Alice", "Dupont", "alice@example.com", "password");
        when(utilisateurService.findByEmail("alice@example.com")).thenReturn(user);

        doThrow(new IllegalArgumentException("Erreur lors de la création"))
                .when(groupeService).creerGroupe(anyString(), anyString(), eq(user));

        mockMvc.perform(MockMvcRequestBuilders.post("/groupes/creer")
                .param("nom", "Groupe Test")
                .param("description", "desc")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/groupes/creer"))
                .andExpect(flash().attribute("errorMessage", "Erreur lors de la création"));
    }

    
    @Test
    @WithMockUser(username = "bob@example.com")
    void testAfficherGroupes() throws Exception {
        Utilisateur user = new Utilisateur("Bob", "Martin", "bob@example.com", "pass");
        when(utilisateurService.findByEmail("bob@example.com")).thenReturn(user);

        Groupe g1 = new Groupe();
        g1.setId(1L);
        g1.setNom("Groupe 1");
        Groupe g2 = new Groupe();
        g2.setId(2L);
        g2.setNom("Groupe 2");

        when(groupeService.getGroupesCreesPar(user)).thenReturn(List.of(g1));
        when(groupeService.getGroupesDisponiblesPour(user)).thenReturn(List.of(g2));
        when(groupeService.getStatutPourUtilisateur(any(), eq(user))).thenReturn(StatutMembre.ACCEPTE);

        mockMvc.perform(MockMvcRequestBuilders.get("/groupes/liste"))
                .andExpect(status().isOk())
                .andExpect(view().name("groupes"))
                .andExpect(model().attributeExists("groupesCrees"))
                .andExpect(model().attributeExists("groupesAutres"))
                .andExpect(model().attributeExists("statuts"))
                .andExpect(model().attributeExists("username"));
    }

    

    
    @Test
    @WithMockUser(username = "charlie@example.com")
    void testAfficherGroupesDisponibles() throws Exception {
        Utilisateur user = new Utilisateur("Charlie", "Brown", "charlie@example.com", "pwd");
        when(utilisateurService.findByEmail("charlie@example.com")).thenReturn(user);

        Groupe g = new Groupe();
        g.setId(3L);
        g.setNom("Groupe dispo");

        when(groupeService.getGroupesDisponiblesPourUtilisateur(user)).thenReturn(List.of(g));
        when(groupeService.getStatutPourUtilisateur(any(), eq(user))).thenReturn(StatutMembre.EN_ATTENTE);

        mockMvc.perform(MockMvcRequestBuilders.get("/groupes/disponibles"))
                .andExpect(status().isOk())
                .andExpect(view().name("groupesDisponibles"))
                .andExpect(model().attributeExists("groupesDisponibles"))
                .andExpect(model().attributeExists("statuts"));
    }

  
    
    @Test
    @WithMockUser(username = "dave@example.com")
    void testRejoindreGroupe_Authenticated() throws Exception {
        Utilisateur user = new Utilisateur("Dave", "Smith", "dave@example.com", "pwd");
        when(utilisateurService.findByEmail("dave@example.com")).thenReturn(user);

        when(groupeService.demanderAdhesion(1L, user)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/groupes/rejoindre")
                .param("idGroupe", "1")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/groupes/disponibles"))
                .andExpect(flash().attribute("message", "Vous avez rejoint le groupe avec succès."));
    }

    // POST /groupes/annuler
    @Test
    @WithMockUser(username = "eve@example.com")
    void testAnnulerDemande() throws Exception {
        Utilisateur user = new Utilisateur("Eve", "Jackson", "eve@example.com", "pwd");
        when(utilisateurService.findByEmail("eve@example.com")).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/groupes/annuler")
                .param("idGroupe", "5")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/groupes/disponibles"))
                .andExpect(flash().attribute("message", "Votre demande a été annulée."));

        verify(groupeService).annulerDemande(5L, user);
    }

    
    
    @Test
    @WithMockUser(username = "frank@example.com")
    void testSupprimerGroupe_Success() throws Exception {
        Utilisateur user = new Utilisateur("Frank", "Miller", "frank@example.com", "pwd");
        when(utilisateurService.findByEmail("frank@example.com")).thenReturn(user);

        Groupe groupe = new Groupe();
        groupe.setId(10L);
        groupe.setCreateur(user);

        when(groupeService.getGroupeById(10L)).thenReturn(groupe);

        mockMvc.perform(MockMvcRequestBuilders.post("/groupes/supprimer")
                .param("idGroupe", "10")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/groupes/liste"))
                .andExpect(flash().attribute("message", "Groupe supprimé avec succès."));

        verify(groupeService).supprimerGroupe(groupe);
    }

    @Test
    @WithMockUser(username = "frank@example.com")
    void testSupprimerGroupe_Unauthorized() throws Exception {
        Utilisateur user = new Utilisateur("Frank", "Miller", "frank@example.com", "pwd");
        Utilisateur autreUser = new Utilisateur("Autre", "User", "autre@example.com", "pwd");

        when(utilisateurService.findByEmail("frank@example.com")).thenReturn(user);

        Groupe groupe = new Groupe();
        groupe.setId(10L);
        groupe.setCreateur(autreUser);

        when(groupeService.getGroupeById(10L)).thenReturn(groupe);

        mockMvc.perform(MockMvcRequestBuilders.post("/groupes/supprimer")
                .param("idGroupe", "10")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/groupes/liste"))
                .andExpect(flash().attribute("errorMessage", "Vous n'êtes pas autorisé à supprimer ce groupe."));

        verify(groupeService, never()).supprimerGroupe(any());
    }

 
    @Test
    @WithMockUser(username = "grace@example.com")
    void testAfficherDemandesEnvoyees() throws Exception {
        Utilisateur user = new Utilisateur("Grace", "Hopper", "grace@example.com", "pwd");
        when(utilisateurService.findByEmail("grace@example.com")).thenReturn(user);

        Groupe groupe = new Groupe();
        groupe.setId(15L);
        groupe.setNom("Groupe en attente");

        when(groupeService.getGroupesAvecDemandeEnAttente(user)).thenReturn(List.of(groupe));

        mockMvc.perform(MockMvcRequestBuilders.get("/groupes/demandes-envoyees"))
                .andExpect(status().isOk())
                .andExpect(view().name("demandesEnvoyees"))
                .andExpect(model().attributeExists("groupes"));
    }

    

    

    @Test
    @WithMockUser(username = "henry@example.com")
    void testGererMembres_Unauthorized() throws Exception {
        Utilisateur user = new Utilisateur("Henry", "Ford", "henry@example.com", "pwd");
        Utilisateur autreUser = new Utilisateur("Autre", "User", "autre@example.com", "pwd");

        when(utilisateurService.findByEmail("henry@example.com")).thenReturn(user);

        Groupe groupe = new Groupe();
        groupe.setId(20L);
        groupe.setCreateur(autreUser);

        when(groupeService.getGroupeById(20L)).thenReturn(groupe);

        mockMvc.perform(MockMvcRequestBuilders.get("/groupes/20/gerer-membres"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/groupes/liste"))
                .andExpect(flash().attribute("errorMessage", "Vous n'êtes pas autorisé à gérer ce groupe."));
    }

   
    @Test
    @WithMockUser(username = "henry@example.com")
    void testGererMembres_GroupeNull() throws Exception {
        Utilisateur user = new Utilisateur("Henry", "Ford", "henry@example.com", "pwd");
        when(utilisateurService.findByEmail("henry@example.com")).thenReturn(user);

        when(groupeService.getGroupeById(999L)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/groupes/999/gerer-membres"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/groupes/liste"))
                .andExpect(flash().attribute("errorMessage", "Vous n'êtes pas autorisé à gérer ce groupe."));
    }
}

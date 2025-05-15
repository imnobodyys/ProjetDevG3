package utcapitole.miage.ProjetDevG3.controllertest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import utcapitole.miage.projetDevG3.Controller.UtilisateurController;
import utcapitole.miage.projetDevG3.Repository.UtilisateurRepository;
import utcapitole.miage.projetDevG3.Service.UtilisateurService;
import utcapitole.miage.projetDevG3.model.Utilisateur;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest
public class UtilisateurControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UtilisateurService utilisateurService;

    @MockBean
    private UtilisateurRepository utilisateurRepository;

    @WithMockUser(username = "test", roles = { "USER" })
    @Test
    void TestSearch() throws Exception {
        Utilisateur utilisateur = new Utilisateur("Alice", "Dupont", "alice@example.com", "1234");

        when(utilisateurService.rechercher("ali")).thenReturn(List.of(utilisateur));

        mockMvc.perform(get("/api/utilisateurs/search").param("q", "ali"))
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(model().attributeExists("resultats"))
                .andExpect(model().attribute("keyword", "ali"));
    }

}

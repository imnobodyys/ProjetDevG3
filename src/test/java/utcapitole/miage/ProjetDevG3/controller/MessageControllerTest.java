
package utcapitole.miage.projetdevg3.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import utcapitole.miage.projetdevg3.config.SecurityConfig;
import utcapitole.miage.projetdevg3.controller.MessageController;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.service.MessageService;
import utcapitole.miage.projetdevg3.repository.UtilisateurRepository;

@WebMvcTest(MessageController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = true)
class MessageControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UtilisateurRepository utilisateurRepository;

    @MockBean
    private MessageService messageService;

    private Utilisateur mockExpediteur;
    private Utilisateur mockDestinataire;

    @BeforeEach
    void setUp() {
        mockExpediteur = new Utilisateur();
        mockExpediteur.setId(1L);
        mockExpediteur.setEmail("user@test.com");

        mockDestinataire = new Utilisateur();
        mockDestinataire.setId(2L);
        mockDestinataire.setEmail("dest@test.com");
    }

    /**
     * test pour entrer page pour modifier message
     * 
     * @throws Exception
     */
    @Test
    @WithMockUser
    void shouldReturnMessageForm() throws Exception {
        mockMvc.perform(get("/messages/envoyer/2"))
                .andExpect(status().isOk())
                .andExpect(view().name("form-message"))
                .andExpect(model().attribute("destinataireId", 2L));
    }

    /**
     * test pour envoyer un message
     * 
     * @throws Exception
     */
    @Test
    @WithMockUser(username = "user@test.com")
    void shouldRedirectAfterSending() throws Exception {
        when(utilisateurRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(mockExpediteur));
        when(utilisateurRepository.findById(2L))
                .thenReturn(Optional.of(mockDestinataire));

        mockMvc.perform(post("/messages/envoyer")
                .param("destinataireId", "2")
                .param("contenu", "Bonjour!")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/demandes/amis"))
                .andExpect(flash().attribute("success", "Message envoyé !"));

        verify(messageService).envoyerMessage(mockExpediteur, mockDestinataire, "Bonjour!");
    }
}
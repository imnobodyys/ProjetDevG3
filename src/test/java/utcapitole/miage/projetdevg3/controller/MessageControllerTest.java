package utcapitole.miage.projetdevg3.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import utcapitole.miage.projetdevg3.model.Message;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.repository.UtilisateurRepository;
import utcapitole.miage.projetdevg3.service.MessageService;
import utcapitole.miage.projetdevg3.service.UtilisateurService;

@WebMvcTest(MessageController.class)
@WithMockUser
class MessageControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private MessageService messageService;

        @MockBean
        private UtilisateurService utilisateurService;

        @MockBean
        private UtilisateurRepository utilisateurRepository;

        private Utilisateur mockUser;
        private Principal mockPrincipal;

        @BeforeEach
        void setUp() {
                mockUser = new Utilisateur();
                mockUser.setId(1L);
                mockUser.setEmail("test@example.com");

                mockPrincipal = () -> "test@example.com";

                when(utilisateurRepository.findByEmail("test@example.com"))
                                .thenReturn(java.util.Optional.of(mockUser));
        }

        @Test
        void testFormulaireMessage() throws Exception {
                mockMvc.perform(get("/messages/envoyer/2").principal(mockPrincipal))
                                .andExpect(status().isOk())
                                .andExpect(view().name("form-message"))
                                .andExpect(model().attribute("destinataireId", 2L));
        }

        @Test
        void testEnvoyerMessagePrive_Success() throws Exception {
                Utilisateur destinataire = new Utilisateur();
                destinataire.setId(2L);

                when(utilisateurRepository.findById(2L))
                                .thenReturn(java.util.Optional.of(destinataire));

                mockMvc.perform(post("/messages/envoyer")
                                .principal(mockPrincipal)
                                .param("destinataireId", "2")
                                .param("contenu", "Bonjour"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/demandes/amis"))
                                .andExpect(flash().attribute("success", "Message envoyé !"));

                verify(messageService).envoyerMessagePrive(eq(1L), eq(destinataire), eq("Bonjour"));
        }

        @Test
        void testEnvoyerMessagePrive_DestinataireNotFound() throws Exception {
                when(utilisateurRepository.findById(2L))
                                .thenReturn(java.util.Optional.empty());

                mockMvc.perform(post("/messages/envoyer")
                                .principal(mockPrincipal)
                                .param("destinataireId", "2")
                                .param("contenu", "Bonjour"))
                                .andExpect(status().isNotFound());
        }

        @Test
        void testEnvoyerMessageGroupe() throws Exception {
                mockMvc.perform(post("/messages/3/messages")
                                .principal(mockPrincipal)
                                .param("contenu", "Message de groupe"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/groupes/3/messages"));

                verify(messageService).envoyerMessageGroupe(eq(3L), any(Utilisateur.class), eq("Message de groupe"));
        }

        @Test
        @WithMockUser(username = "unknown@example.com")
        void testGetCurrentUser_UserNotFound() throws Exception {
                when(utilisateurRepository.findByEmail("unknown@example.com"))
                                .thenReturn(java.util.Optional.empty());

                mockMvc.perform(get("/messages/list")
                                .principal(() -> "unknown@example.com"))
                                .andExpect(status().is4xxClientError());
        }
}
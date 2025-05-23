package utcapitole.miage.projetdevg3.controller;

import static org.mockito.ArgumentMatchers.any;
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

import utcapitole.miage.projetdevg3.model.ConversationGrp;
import utcapitole.miage.projetdevg3.model.ConversationPri;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.repository.UtilisateurRepository;
import utcapitole.miage.projetdevg3.service.ConversationService;
import utcapitole.miage.projetdevg3.service.MessageService;

@WebMvcTest(ConversationController.class)
@WithMockUser
class ConversationControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private ConversationService conversationService;

        @MockBean
        private MessageService messageService;

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
        @WithMockUser(username = "unknown@example.com")
        void testGetCurrentUser_UserNotFound() throws Exception {
                when(utilisateurRepository.findByEmail("unknown@example.com"))
                                .thenReturn(java.util.Optional.empty());

                mockMvc.perform(get("/conversations")
                                .principal(() -> "unknown@example.com"))
                                .andExpect(status().is4xxClientError());
        }
}
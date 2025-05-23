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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import utcapitole.miage.projetdevg3.model.ConversationGrp;
import utcapitole.miage.projetdevg3.model.ConversationPri;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.repository.UtilisateurRepository;
import utcapitole.miage.projetdevg3.service.ConversationService;
import utcapitole.miage.projetdevg3.service.MessageService;

/**
 * Classe de test pour le contrôleur des conversations.
 * Ce test couvre l'affichage du centre de messages ainsi que l'envoi d'un
 * message de groupe.
 */
@WebMvcTest(ConversationController.class)
@WithMockUser(username = "test@example.com")
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
        private Utilisateur otherUser;
        private Principal mockPrincipal;
        private ConversationPri privateConv;
        private ConversationGrp groupConv;

        /**
         * Configuration initiale avant chaque test :
         * Crée un utilisateur simulé et configure le repository pour le retourner.
         */

        @BeforeEach
        void setUp() {
                mockUser = new Utilisateur();
                mockUser.setId(1L);
                mockUser.setEmail("test@example.com");
                mockUser.setNom("Test User");

                otherUser = new Utilisateur();
                otherUser.setId(2L);
                otherUser.setEmail("other@example.com");
                otherUser.setNom("Other User");

                mockPrincipal = () -> "test@example.com";

                privateConv = new ConversationPri();
                privateConv.setId(1L);
                privateConv.setExpediteurCP(mockUser);
                privateConv.setDestinataireCP(otherUser);
                privateConv.setRecentMessages(Collections.emptyList());

                groupConv = new ConversationGrp();
                groupConv.setId(1L);
                groupConv.setRecentMessages(Collections.emptyList());
                when(utilisateurRepository.findByEmail("test@example.com"))
                                .thenReturn(java.util.Optional.of(mockUser));
        }

        /**
         * Teste le comportement lorsque l'utilisateur est introuvable dans la base.
         * Attend une erreur 4xx.
         */
        @Test
        @WithMockUser(username = "unknown@example.com")
        void testGetCurrentUser_UserNotFound() throws Exception {
                when(utilisateurRepository.findByEmail("unknown@example.com"))
                                .thenReturn(java.util.Optional.empty());

                mockMvc.perform(get("/conversations")
                                .principal(() -> "unknown@example.com"))
                                .andExpect(status().is4xxClientError());
        }

        @Test
        void testAfficherCentreMessages_NoConversations() throws Exception {

                when(conversationService.getPrivateConversationsWithRecentMessages(mockUser))
                                .thenReturn(Collections.emptyList());
                when(conversationService.getGroupConversationsWithRecentMessages(mockUser))
                                .thenReturn(Collections.emptyList());

                mockMvc.perform(get("/conversations")
                                .with(csrf())
                                .principal(mockPrincipal))
                                .andExpect(status().isOk())
                                .andExpect(view().name("conversation"))
                                .andExpect(model().attribute("privateConvs", Collections.emptyList()))
                                .andExpect(model().attribute("groupConvs", Collections.emptyList()));
        }

}

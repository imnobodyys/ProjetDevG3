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
    void testAfficherCentreMessages() throws Exception {
        // Mock data
        ConversationPri privateConv = new ConversationPri();
        ConversationGrp groupConv = new ConversationGrp();

        when(conversationService.getConversationsWithRecentMessages(any(Utilisateur.class)))
                .thenReturn(Arrays.asList(privateConv));
        when(conversationService.getGroupConversationsWithRecentMessages(any(Utilisateur.class)))
                .thenReturn(Arrays.asList(groupConv));

        // Test & Verify
        mockMvc.perform(get("/conversations").principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(view().name("message-centre"))
                .andExpect(model().attributeExists("privateConvs"))
                .andExpect(model().attributeExists("groupConvs"));
    }

    @Test
    void testAfficherCentreMessages_NoConversations() throws Exception {
        when(conversationService.getConversationsWithRecentMessages(any(Utilisateur.class)))
                .thenReturn(Collections.emptyList());
        when(conversationService.getGroupConversationsWithRecentMessages(any(Utilisateur.class)))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/conversations").principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(model().attribute("privateConvs", Collections.emptyList()))
                .andExpect(model().attribute("groupConvs", Collections.emptyList()));
    }

    @Test
    void testEnvoyerMessagePrive() throws Exception {
        mockMvc.perform(post("/conversations/privee/1")
                .principal(mockPrincipal)
                .param("contenu", "Bonjour"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/conversations#1"));
    }

    @Test
    void testEnvoyerMessageGroupe() throws Exception {
        mockMvc.perform(post("/conversations/groupe/2")
                .principal(mockPrincipal)
                .param("contenu", "Message de groupe"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/conversations#groupe-2"));
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
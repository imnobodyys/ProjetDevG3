package utcapitole.miage.projetDevG3.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import utcapitole.miage.projetDevG3.Controller.ConversationController;
import utcapitole.miage.projetDevG3.Service.ConversationService;
import utcapitole.miage.projetDevG3.config.SecurityConfig;
import utcapitole.miage.projetDevG3.model.ConversationPri;
import utcapitole.miage.projetDevG3.model.Message;
import utcapitole.miage.projetDevG3.model.Utilisateur;
import utcapitole.miage.projetDevG3.Repository.UtilisateurRepository;

@WebMvcTest(controllers = ConversationController.class)
@Import(SecurityConfig.class)
public class ConversationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConversationService conversationService;

    @MockBean
    private UtilisateurRepository utilisateurRepository;

    private Utilisateur utilisateur;

    @BeforeEach
    void setUp() {
        utilisateur = new Utilisateur();
        utilisateur.setId(1L);
        utilisateur.setEmail("test@example.com");
        utilisateur.setNom("Testeur");

        // 构造 ConversationPri 的数据（expediteurCP 和 destinataireCP 都非空）
        Utilisateur expediteur = new Utilisateur();
        expediteur.setId(2L);
        expediteur.setNom("Alice");

        Utilisateur destinataire = new Utilisateur();
        destinataire.setId(3L);
        destinataire.setNom("Bob");

        ConversationPri conversation1 = new ConversationPri();
        conversation1.setId(10L);
        conversation1.setExpediteurCP(expediteur);
        conversation1.setDestinataireCP(destinataire);

        ConversationPri conversation2 = new ConversationPri();
        conversation2.setId(11L);
        conversation2.setExpediteurCP(destinataire);
        conversation2.setDestinataireCP(expediteur);

        List<ConversationPri> mockConversations = Arrays.asList(conversation1, conversation2);
        when(utilisateurRepository.findByEmail("test@example.com")).thenReturn(Optional.of(utilisateur));
        when(conversationService.getConversationsOfUser(utilisateur)).thenReturn(mockConversations);
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void afficherConversations_ShouldReturnConversationView() throws Exception {
        mockMvc.perform(get("/conversations/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("conversation"))
                .andExpect(model().attributeExists("conversations"))
                .andExpect(model().attribute("conversations", hasSize(2)));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void afficherMessages_ShouldReturnMessagesView() throws Exception {
        // 构造用户
        Utilisateur expediteur = new Utilisateur();
        expediteur.setId(1L);
        expediteur.setEmail("test@example.com");
        expediteur.setNom("Alice");

        Utilisateur autre = new Utilisateur();
        autre.setId(2L);
        autre.setEmail("ami@example.com");
        autre.setNom("Bob");

        // 构造消息
        Message msg1 = new Message();
        msg1.setId(100L);
        msg1.setContenu("Bonjour !");
        msg1.setExpedi(expediteur);

        Message msg2 = new Message();
        msg2.setId(101L);
        msg2.setContenu("Salut !");
        msg2.setExpedi(autre);

        List<Message> messages = Arrays.asList(msg1, msg2);

        when(utilisateurRepository.findByEmail("test@example.com")).thenReturn(Optional.of(utilisateur));
        when(conversationService.getMessagesForConversation(1L, utilisateur)).thenReturn(messages);
        when(conversationService.getOtherUser(1L, utilisateur)).thenReturn(autre);

        mockMvc.perform(get("/conversations/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("messages"))
                .andExpect(model().attributeExists("messages"))
                .andExpect(model().attributeExists("autreUtilisateur"));
    }

}

package utcapitole.miage.projetdevg3.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.security.Principal;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import utcapitole.miage.projetdevg3.config.SecurityConfig;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.repository.ConversationGrpRepository;
import utcapitole.miage.projetdevg3.repository.ConversationPriRepository;
import utcapitole.miage.projetdevg3.repository.MessageRepository;
import utcapitole.miage.projetdevg3.repository.UtilisateurRepository;
import utcapitole.miage.projetdevg3.service.MessageService;
import utcapitole.miage.projetdevg3.service.UtilisateurService;

@WebMvcTest(MessageController.class)
@Import(SecurityConfig.class)
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

        @MockBean
        private MessageRepository messageRepository;

        @MockBean
        private ConversationPriRepository conversationPriRepository;

        @MockBean
        private ConversationGrpRepository conversationGrpRepository;

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

        /**
         * Test1 - Affichage du formulaire d'envoi de message
         * Doit retourner la vue avec le destinataire valide
         */
        @Test
        void afficherFormulaireMessage_QuandDestinataireExiste_DoitAfficherFormulaire() throws Exception {
                // Arrange
                Utilisateur destinataire = new Utilisateur();
                destinataire.setId(2L);
                when(utilisateurRepository.findById(2L)).thenReturn(Optional.of(destinataire));

                // Act & Assert
                mockMvc.perform(get("/messages/envoyer/2"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("form-message"))
                                .andExpect(model().attributeExists("destinataire"));
        }

}

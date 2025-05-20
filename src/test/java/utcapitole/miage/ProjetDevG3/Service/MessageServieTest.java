package utcapitole.miage.projetdevg3.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import utcapitole.miage.projetdevg3.model.*;
import utcapitole.miage.projetdevg3.repository.*;

/**
 * Classe de test pour {@link MessageService}
 */
@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

        @Mock
        private ConversationPriRepository conversationPriRepository;

        @Mock
        private MessageRepository messageRepository;

        @InjectMocks
        private MessageService messageService;

        private Utilisateur expediteur;
        private Utilisateur destinataire;
        private ConversationPri conversationpri;
        private ConversationGrp conversationGrp;

        /**
         * Initialise les données de test avant chaque méthode de test
         */
        @BeforeEach
        void setUp() {
                expediteur = creerUtilisateurTest(1L, "Alice");
                destinataire = creerUtilisateurTest(2L, "Bob");
                conversationpri = creerConversationPriveeTest(expediteur, destinataire);
                conversationGrp = creerConversationGroupeTest();
        }

        /**
         * Teste la création d'une nouvelle conversation quand elle n'existe pas
         */
        @Test
        void shouldCreateNewConversationWhenNotExists() {
                when(conversationPriRepository.findByExpediteurCPAndDestinataireCPOrExpediteurCPAndDestinataireCP(
                                any(), any(), any(), any())).thenReturn(Optional.empty());

                ConversationPri savedConversation = creerConversationPriveeTest(expediteur, destinataire);
                when(conversationPriRepository.save(any())).thenReturn(savedConversation);

                messageService.envoyerMessage(expediteur, destinataire, "Bonjour");

                verify(conversationPriRepository).save(any(ConversationPri.class));
                verify(messageRepository).save(argThat(message -> message.getContenu().equals("Bonjour") &&
                                message.getExpedi().getId().equals(expediteur.getId()) &&
                                message.getConversation().getId() == savedConversation.getId()));
        }

        /**
         * Teste l'utilisation d'une conversation existante
         */
        @Test
        void shouldUseExistingConversationWhenFound() {
                ConversationPri existingConversation = creerConversationPriveeTest(expediteur, destinataire);

                when(conversationPriRepository.findByExpediteurCPAndDestinataireCPOrExpediteurCPAndDestinataireCP(
                                eq(expediteur), eq(destinataire), eq(destinataire), eq(expediteur)))
                                .thenReturn(Optional.of(existingConversation));

                messageService.envoyerMessage(expediteur, destinataire, "Comment ça va?");

                verify(conversationPriRepository, never()).save(any());
                verify(messageRepository).save(argThat(
                                message -> message.getConversation().getId() == existingConversation.getId()));
        }

        /**
         * Teste l'envoi d'un message dans un groupe
         */
        @Test
        void envoyerMessageGP_shouldSaveAndReturnMessage() {
                String contenu = "Message de test";
                Message messageTest = creerMessageTest(contenu, conversationGrp);

                when(messageRepository.save(any(Message.class))).thenReturn(messageTest);

                Message result = messageService.envoyerMessageGP(expediteur, conversationGrp, contenu);

                assertNotNull(result, "Le message retourné ne devrait pas être null");
                assertEquals(expediteur.getId(), result.getExpedi().getId(), "L'expéditeur ne correspond pas");
                assertEquals(contenu, result.getContenu(), "Le contenu ne correspond pas");
                assertEquals(conversationGrp.getId(), result.getConversation().getId(),
                                "La conversation ne correspond pas");
        }

        /**
         * Teste la récupération des messages privés récents
         */
        @Test
        void getRecentPriMessages_shouldReturnLimitedMessages() {
                Message message1 = creerMessageTest("Message 1", conversationpri);
                Message message2 = creerMessageTest("Message 2", conversationpri);

                when(messageRepository.findPrivateMessagesForUser(eq(expediteur), any(Pageable.class)))
                                .thenReturn(Arrays.asList(message1, message2));

                List<Message> result = messageService.getRecentPriMessages(expediteur);

                assertEquals(2, result.size(), "Devrait retourner 2 messages");
                assertTrue(result.stream().anyMatch(m -> m.getContenu().equals("Message 1")),
                                "Devrait contenir le message 1");
        }

        // Méthodes utilitaires pour créer les objets de test

        /**
         * Crée un utilisateur de test
         * 
         * @param id  L'identifiant de l'utilisateur
         * @param nom Le nom de l'utilisateur
         * @return L'utilisateur créé
         */
        private Utilisateur creerUtilisateurTest(Long id, String nom) {
                Utilisateur utilisateur = new Utilisateur();
                utilisateur.setId(id);
                utilisateur.setNom(nom);
                return utilisateur;
        }

        /**
         * Crée une conversation privée de test
         * 
         * @param expediteur   L'expéditeur de la conversation
         * @param destinataire Le destinataire de la conversation
         * @return La conversation créée
         */
        private ConversationPri creerConversationPriveeTest(Utilisateur expediteur, Utilisateur destinataire) {
                ConversationPri conversation = new ConversationPri();
                conversation.setId(100L);
                conversation.setExpediteurCP(expediteur);
                conversation.setDestinataireCP(destinataire);
                return conversation;
        }

        /**
         * Crée une conversation de groupe de test
         * 
         * @return La conversation de groupe créée
         */
        private ConversationGrp creerConversationGroupeTest() {
                ConversationGrp conversation = new ConversationGrp();
                conversation.setId(200L);
                // Ajouter d'autres champs nécessaires
                return conversation;
        }

        /**
         * Crée un message de test
         * 
         * @param contenu      Le contenu du message
         * @param conversation La conversation associée
         * @return Le message créé
         */
        private Message creerMessageTest(String contenu, Conversation conversation) {
                Message message = new Message();
                message.setId(300L);
                message.setContenu(contenu);
                message.setExpedi(expediteur);
                message.setConversation(conversation);
                message.setDtEnvoi(LocalDateTime.now());
                return message;
        }
}
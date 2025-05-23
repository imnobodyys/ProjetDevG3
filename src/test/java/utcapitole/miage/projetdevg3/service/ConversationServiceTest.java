package utcapitole.miage.projetdevg3.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import utcapitole.miage.projetdevg3.model.ConversationGrp;
import utcapitole.miage.projetdevg3.model.ConversationPri;
import utcapitole.miage.projetdevg3.model.Message;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.repository.ConversationGrpRepository;
import utcapitole.miage.projetdevg3.repository.ConversationPriRepository;
import utcapitole.miage.projetdevg3.repository.MessageRepository;

@ExtendWith(MockitoExtension.class)
class ConversationServiceTest {

        @Mock
        private ConversationPriRepository conversationPriRepository;

        @Mock
        private ConversationGrpRepository conversationGrpRepository;

        @Mock
        private MessageRepository messageRepository;

        @InjectMocks
        private ConversationService conversationService;

        private Utilisateur mockUser;
        private Utilisateur otherUser;
        private ConversationPri mockPrivateConv;
        private ConversationGrp mockGroupConv;
        private Message mockMessage;
        private static final Pageable TOP_FIVE = PageRequest.of(0, 5);

        @BeforeEach
        void setUp() {
                mockUser = new Utilisateur();
                mockUser.setId(1L);
                mockUser.setEmail("user1@example.com");

                otherUser = new Utilisateur();
                otherUser.setId(2L);
                otherUser.setEmail("user2@example.com");

                mockPrivateConv = new ConversationPri();
                mockPrivateConv.setId(1L);
                mockPrivateConv.setExpediteurCP(mockUser);
                mockPrivateConv.setDestinataireCP(otherUser);

                mockGroupConv = new ConversationGrp();
                mockGroupConv.setId(2L);

                mockMessage = new Message();
                mockMessage.setId(1L);
                mockMessage.setContenu("Test message");
                mockMessage.setExpedi(mockUser);
        }

        /**
         * Vérifie la récupération des messages d'une conversation valide.
         */
        @Test
        void testGetMessagesForConversation_Success() {
                when(conversationPriRepository.findById(1L))
                                .thenReturn(Optional.of(mockPrivateConv));
                when(messageRepository.findByConversationOrderByDtEnvoiAsc(mockPrivateConv))
                                .thenReturn(Arrays.asList(mockMessage));

                List<Message> result = conversationService.getMessagesForConversation(1L, mockUser);

                assertEquals(1, result.size());
                verify(conversationPriRepository).findById(1L);
                verify(messageRepository).findByConversationOrderByDtEnvoiAsc(mockPrivateConv);
        }

        /**
         * Vérifie qu'une exception est levée si la conversation est introuvable.
         */
        @Test
        void testGetMessagesForConversation_NotFound() {
                when(conversationPriRepository.findById(1L))
                                .thenReturn(Optional.empty());

                assertThrows(IllegalArgumentException.class, () -> {
                        conversationService.getMessagesForConversation(1L, mockUser);
                });
        }

        /**
         * Vérifie qu'un accès non autorisé lève une exception.
         */
        @Test
        void testGetMessagesForConversation_Unauthorized() {
                Utilisateur unauthorizedUser = new Utilisateur();
                unauthorizedUser.setId(3L);

                when(conversationPriRepository.findById(1L))
                                .thenReturn(Optional.of(mockPrivateConv));

                assertThrows(IllegalStateException.class, () -> {
                        conversationService.getMessagesForConversation(1L, unauthorizedUser);
                });
        }

        /**
         * Vérifie la récupération de l'autre utilisateur dans une conversation.
         */

        @Test
        void testGetOtherUser_Success() {
                when(conversationPriRepository.findById(1L))
                                .thenReturn(Optional.of(mockPrivateConv));

                Utilisateur result = conversationService.getOtherUser(1L, mockUser);

                assertEquals(otherUser.getId(), result.getId());
                verify(conversationPriRepository).findById(1L);
        }

        /**
         * Vérifie qu'une exception est levée si la conversation est absente.
         */
        @Test
        void testGetOtherUser_NotFound() {
                when(conversationPriRepository.findById(1L))
                                .thenReturn(Optional.empty());

                assertThrows(IllegalArgumentException.class, () -> {
                        conversationService.getOtherUser(1L, mockUser);
                });
        }

        /**
         * Vérifie l'inversion correcte des rôles dans une conversation.
         */
        @Test
        void testGetOtherUser_AsRecipient() {
                when(conversationPriRepository.findById(1L))
                                .thenReturn(Optional.of(mockPrivateConv));

                Utilisateur result = conversationService.getOtherUser(1L, otherUser);

                assertEquals(mockUser.getId(), result.getId());
        }

        /**
         * Vérifie que les messages récents sont chargés dans les conversations privées.
         */
        @Test
        void testGetPrivateConversationsWithRecentMessages() {
                List<ConversationPri> conversations = List.of(mockPrivateConv);
                List<Message> recent = List.of(mockMessage);

                when(conversationPriRepository.findByExpediteurCPOrDestinataireCP(mockUser, mockUser))
                                .thenReturn(conversations);
                when(messageRepository.findByConversationOrderByDtEnvoiDesc(mockPrivateConv, PageRequest.of(0, 5)))
                                .thenReturn(recent);

                List<ConversationPri> result = conversationService.getPrivateConversationsWithRecentMessages(mockUser);

                assertEquals(1, result.size());
                assertEquals(recent, result.get(0).getRecentMessages());
        }

        /**
         * Vérifie que les messages récents sont chargés dans les conversations de
         * groupe.
         */
        @Test
        void testGetGroupConversationsWithRecentMessages() {
                List<ConversationGrp> conversations = List.of(mockGroupConv);
                List<Message> recent = List.of(mockMessage);

                when(conversationGrpRepository.findByGroupeCon_Membres_membreUtilisateur(mockUser))
                                .thenReturn(conversations);
                when(messageRepository.findByConversationOrderByDtEnvoiDesc(mockGroupConv, PageRequest.of(0, 5)))
                                .thenReturn(recent);

                List<ConversationGrp> result = conversationService.getGroupConversationsWithRecentMessages(mockUser);

                assertEquals(1, result.size());
                assertEquals(recent, result.get(0).getRecentMessages());
        }

        /**
         * Vérifie que la méthode retourne une conversation existante ou la crée si
         * absente.
         */
        @Test
        void testFindOrCreatePrivateConversation_NewConversation() {
                when(conversationPriRepository.findConversationBetween(mockUser, otherUser))
                                .thenReturn(Optional.empty());
                when(conversationPriRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

                ConversationPri result = conversationService.findOrCreatePrivateConversation(mockUser, otherUser);

                assertEquals(mockUser, result.getExpediteurCP());
                assertEquals(otherUser, result.getDestinataireCP());
                verify(conversationPriRepository).save(any());
        }

        /**
         * Vérifie que la méthode retourne une conversation existante si trouvée.
         */
        @Test
        void testFindOrCreatePrivateConversation_Existing() {
                when(conversationPriRepository.findConversationBetween(mockUser, otherUser))
                                .thenReturn(Optional.of(mockPrivateConv));

                ConversationPri result = conversationService.findOrCreatePrivateConversation(mockUser, otherUser);

                assertEquals(mockPrivateConv, result);
                verify(conversationPriRepository, never()).save(any());
        }

        /**
         * Vérifie que les messages sont correctement retournés pour un groupe.
         */
        @Test
        void testGetMessagesForGroup_Success() {
                mockGroupConv.setMessages(List.of(mockMessage));
                when(conversationGrpRepository.findByGroupeCon_Id(2L)).thenReturn(Optional.of(mockGroupConv));

                List<Message> result = conversationService.getMessagesForGroup(2L);

                assertEquals(1, result.size());
                assertEquals(mockMessage.getContenu(), result.get(0).getContenu());
        }

        /**
         * Vérifie que l'absence de groupe lève une exception.
         */
        @Test
        void testGetMessagesForGroup_NotFound() {
                when(conversationGrpRepository.findByGroupeCon_Id(99L)).thenReturn(Optional.empty());

                assertThrows(IllegalArgumentException.class, () -> {
                        conversationService.getMessagesForGroup(99L);
                });
        }
}
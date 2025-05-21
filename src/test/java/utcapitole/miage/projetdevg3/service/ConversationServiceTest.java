package utcapitole.miage.projetdevg3.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import utcapitole.miage.projetdevg3.model.*;
import utcapitole.miage.projetdevg3.repository.ConversationPriRepository;
import utcapitole.miage.projetdevg3.repository.MessageRepository;
import utcapitole.miage.projetdevg3.service.ConversationService;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class ConversationServiceTest {

        @Mock
        private ConversationPriRepository conversationPriRepository;

        @Mock
        private MessageRepository messageRepository;

        @InjectMocks
        private ConversationService conversationService;

        private Utilisateur user;
        private Utilisateur otherUser;
        private ConversationPri conversation;

        @BeforeEach
        void setUp() {
                user = new Utilisateur();
                user.setId(1L);
                user.setEmail("user@test.com");

                otherUser = new Utilisateur();
                otherUser.setId(2L);
                otherUser.setEmail("other@test.com");

                conversation = new ConversationPri();
                conversation.setId(100L);
                conversation.setExpediteurCP(user);
                conversation.setDestinataireCP(otherUser);
        }

        @Test
        void getConversationsOfUser_shouldReturnList() {
                List<ConversationPri> mockList = List.of(conversation);
                when(conversationPriRepository.findByExpediteurCPOrDestinataireCP(user, user))
                                .thenReturn(mockList);

                List<ConversationPri> result = conversationService.getConversationsOfUser(user);

                assertEquals(1, result.size());
                assertEquals(conversation, result.get(0));
        }

        @Test
        void getMessagesForConversation_shouldReturnMessages() {
                Message message1 = new Message();
                message1.setDtEnvoi(LocalDateTime.now());

                when(conversationPriRepository.findById(100L))
                                .thenReturn(Optional.of(conversation));
                when(messageRepository.findByConversationOrderByDtEnvoiAsc(conversation))
                                .thenReturn(List.of(message1));

                List<Message> messages = conversationService.getMessagesForConversation(100L, user);

                assertEquals(1, messages.size());
        }

        @Test
        void getMessagesForConversation_notParticipant_shouldThrow() {
                Utilisateur stranger = new Utilisateur();
                stranger.setId(99L);

                when(conversationPriRepository.findById(100L))
                                .thenReturn(Optional.of(conversation));

                assertThrows(IllegalStateException.class,
                                () -> conversationService.getMessagesForConversation(100L, stranger));
        }

        @Test
        void getMessagesForConversation_notFound_shouldThrow() {
                when(conversationPriRepository.findById(999L))
                                .thenReturn(Optional.empty());

                assertThrows(IllegalArgumentException.class,
                                () -> conversationService.getMessagesForConversation(999L, user));
        }

        @Test
        void getOtherUser_shouldReturnOtherParticipant() {
                when(conversationPriRepository.findById(100L))
                                .thenReturn(Optional.of(conversation));

                Utilisateur result = conversationService.getOtherUser(100L, user);

                assertEquals(otherUser, result);
        }

        @Test
        void getOtherUser_notFound_shouldThrow() {
                when(conversationPriRepository.findById(999L))
                                .thenReturn(Optional.empty());

                assertThrows(IllegalArgumentException.class,
                                () -> conversationService.getOtherUser(999L, user));
        }
}

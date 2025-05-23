package utcapitole.miage.projetdevg3.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
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

import jakarta.transaction.Transactional;
import utcapitole.miage.projetdevg3.model.ConversationGrp;
import utcapitole.miage.projetdevg3.model.ConversationPri;
import utcapitole.miage.projetdevg3.model.Message;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.repository.ConversationGrpRepository;
import utcapitole.miage.projetdevg3.repository.ConversationPriRepository;
import utcapitole.miage.projetdevg3.repository.MessageRepository;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

        @Mock
        private MessageRepository messageRepository;

        @Mock
        private ConversationPriRepository conversationPriRepository;

        @Mock
        private ConversationGrpRepository conversationGrpRepository;

        @InjectMocks
        private MessageService messageService;

        private Utilisateur mockUser;
        private ConversationPri mockPrivateConv;
        private ConversationGrp mockGroupConv;
        private Message mockMessage;
        private static final Pageable TOP_FIVE = PageRequest.of(0, 5);

        @BeforeEach
        void setUp() {
                mockUser = new Utilisateur();
                mockUser.setId(1L);
                mockUser.setEmail("test@example.com");

                mockPrivateConv = new ConversationPri();
                mockPrivateConv.setId(1L);

                mockGroupConv = new ConversationGrp();
                mockGroupConv.setId(2L);

                mockMessage = new Message();
                mockMessage.setId(1L);
                mockMessage.setContenu("Test message");
                mockMessage.setDtEnvoi(LocalDateTime.now());
                mockMessage.setExpedi(mockUser);
        }

        @Test
        @Transactional
        void testEnvoyerMessagePrive_Success() {
                when(conversationPriRepository.findById(1L))
                                .thenReturn(Optional.of(mockPrivateConv));
                when(messageRepository.save(any(Message.class)))
                                .thenReturn(mockMessage);

                messageService.envoyerMessagePrive(1L, mockUser, "Test message");

                verify(conversationPriRepository).findById(1L);
                verify(messageRepository).save(any(Message.class));
        }

        @Test
        @Transactional
        void testEnvoyerMessagePrive_ConversationNotFound() {
                when(conversationPriRepository.findById(1L))
                                .thenReturn(Optional.empty());

                assertThrows(IllegalArgumentException.class, () -> {
                        messageService.envoyerMessagePrive(1L, mockUser, "Test message");
                });
        }

        @Test
        @Transactional
        void testEnvoyerMessageGroupe_Success() {
                when(conversationGrpRepository.findById(2L))
                                .thenReturn(Optional.of(mockGroupConv));
                when(messageRepository.save(any(Message.class)))
                                .thenReturn(mockMessage);

                messageService.envoyerMessageGroupe(2L, mockUser, "Test group message");

                verify(conversationGrpRepository).findById(2L);
                verify(messageRepository).save(any(Message.class));
        }

        @Test
        @Transactional
        void testEnvoyerMessageGroupe_ConversationNotFound() {
                when(conversationGrpRepository.findById(2L))
                                .thenReturn(Optional.empty());

                assertThrows(IllegalArgumentException.class, () -> {
                        messageService.envoyerMessageGroupe(2L, mockUser, "Test group message");
                });
        }

        @Test
        void testGetMessagesByConversationId() {
                when(messageRepository.findByConversationId(1L))
                                .thenReturn(Arrays.asList(mockMessage));

                List<Message> messages = messageService.getMessagesByConversationId(1L);

                assertEquals(1, messages.size());
                assertEquals("Test message", messages.get(0).getContenu());
        }

        @Test
        void testGetRecentGroupMessages() {
                when(messageRepository.findGroupMessagesForUser(mockUser, TOP_FIVE))
                                .thenReturn(Arrays.asList(mockMessage));

                List<Message> messages = messageService.getRecentGroupMessages(mockUser);

                assertEquals(1, messages.size());
                verify(messageRepository).findGroupMessagesForUser(mockUser, TOP_FIVE);
        }

        @Test
        void testGetRecentGroupMessages_EmptyList() {
                when(messageRepository.findGroupMessagesForUser(mockUser, TOP_FIVE))
                                .thenReturn(Collections.emptyList());

                List<Message> messages = messageService.getRecentGroupMessages(mockUser);

                assertTrue(messages.isEmpty());
        }
}
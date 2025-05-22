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

        @Test
        void testGetGroupConversationsWithRecentMessages() {
                when(conversationGrpRepository.findByUtilisateur(mockUser))
                                .thenReturn(Arrays.asList(mockGroupConv));
                when(messageRepository.findByConversationOrderByDtEnvoiDesc(mockGroupConv, TOP_FIVE))
                                .thenReturn(Arrays.asList(mockMessage));

                List<ConversationGrp> result = conversationService.getGroupConversationsWithRecentMessages(mockUser);

                assertEquals(1, result.size());
                assertEquals(1, result.get(0).getRecentMessages().size());
                verify(conversationGrpRepository).findByUtilisateur(mockUser);
                verify(messageRepository).findByConversationOrderByDtEnvoiDesc(mockGroupConv, TOP_FIVE);
        }

        @Test
        void testGetGroupConversationsWithRecentMessages_EmptyList() {
                when(conversationGrpRepository.findByUtilisateur(mockUser))
                                .thenReturn(Collections.emptyList());

                List<ConversationGrp> result = conversationService.getGroupConversationsWithRecentMessages(mockUser);

                assertTrue(result.isEmpty());
                verify(conversationGrpRepository).findByUtilisateur(mockUser);
                verify(messageRepository, never()).findByConversationOrderByDtEnvoiDesc(any(), any());
        }

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

        @Test
        void testGetMessagesForConversation_NotFound() {
                when(conversationPriRepository.findById(1L))
                                .thenReturn(Optional.empty());

                assertThrows(IllegalArgumentException.class, () -> {
                        conversationService.getMessagesForConversation(1L, mockUser);
                });
        }

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

        @Test
        void testGetOtherUser_Success() {
                when(conversationPriRepository.findById(1L))
                                .thenReturn(Optional.of(mockPrivateConv));

                Utilisateur result = conversationService.getOtherUser(1L, mockUser);

                assertEquals(otherUser.getId(), result.getId());
                verify(conversationPriRepository).findById(1L);
        }

        @Test
        void testGetOtherUser_NotFound() {
                when(conversationPriRepository.findById(1L))
                                .thenReturn(Optional.empty());

                assertThrows(IllegalArgumentException.class, () -> {
                        conversationService.getOtherUser(1L, mockUser);
                });
        }

        @Test
        void testGetOtherUser_AsRecipient() {
                when(conversationPriRepository.findById(1L))
                                .thenReturn(Optional.of(mockPrivateConv));

                Utilisateur result = conversationService.getOtherUser(1L, otherUser);

                assertEquals(mockUser.getId(), result.getId());
        }
}
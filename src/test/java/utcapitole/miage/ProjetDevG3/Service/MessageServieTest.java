package utcapitole.miage.projetdevg3.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import utcapitole.miage.projetdevg3.model.ConversationPri;

import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.repository.ConversationPriRepository;

import utcapitole.miage.projetdevg3.repository.MessageRepository;

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

        @BeforeEach
        void setUp() {
                expediteur = new Utilisateur();
                expediteur.setId(1L);
                expediteur.setNom("Alice");

                destinataire = new Utilisateur();
                destinataire.setId(2L);
                destinataire.setNom("Bob");
        }

        /**
         * creer un nouvelle conversation
         */
        @Test
        void shouldCreateNewConversationWhenNotExists() {

                when(conversationPriRepository.findByExpediteurCPAndDestinataireCPOrExpediteurCPAndDestinataireCP(
                                any(), any(), any(), any()))
                                .thenReturn(Optional.empty());

                ConversationPri savedConversation = new ConversationPri();
                savedConversation.setExpediteurCP(expediteur);
                savedConversation.setDestinataireCP(destinataire);
                when(conversationPriRepository.save(any())).thenReturn(savedConversation);

                messageService.envoyerMessage(expediteur, destinataire, "Bonjour");

                verify(conversationPriRepository).save(any(ConversationPri.class));
                verify(messageRepository).save(argThat(message -> message.getContenu().equals("Bonjour") &&
                                message.getExpedi().equals(expediteur) &&
                                message.getConversation().equals(savedConversation)));
        }

        /**
         * test quand il y a conversation
         */
        @Test
        void shouldUseExistingConversationWhenFound() {

                ConversationPri existingConversation = new ConversationPri();
                existingConversation.setId(10L);
                existingConversation.setExpediteurCP(expediteur);
                existingConversation.setDestinataireCP(destinataire);

                when(conversationPriRepository.findByExpediteurCPAndDestinataireCPOrExpediteurCPAndDestinataireCP(
                                eq(expediteur), eq(destinataire), eq(destinataire), eq(expediteur)))
                                .thenReturn(Optional.of(existingConversation));

                messageService.envoyerMessage(expediteur, destinataire, "Comment ça va?");

                verify(conversationPriRepository, never()).save(any());
                verify(messageRepository).save(argThat(message -> message.getConversation().getId() == 10L &&
                                message.getContenu().equals("Comment ça va?")));
        }

        /**
         * test pour conversation entre deux personnes,
         */
        @Test
        void shouldFindConversationInBothDirections() {

                ConversationPri reverseConversation = new ConversationPri();
                reverseConversation.setExpediteurCP(destinataire);
                reverseConversation.setDestinataireCP(expediteur);

                when(conversationPriRepository.findByExpediteurCPAndDestinataireCPOrExpediteurCPAndDestinataireCP(
                                eq(expediteur), eq(destinataire), eq(destinataire), eq(expediteur)))
                                .thenReturn(Optional.of(reverseConversation));

                messageService.envoyerMessage(expediteur, destinataire, "Salut");

                verify(messageRepository)
                                .save(argThat(message -> {
                                        ConversationPri conv = (ConversationPri) message.getConversation();
                                        return conv.getExpediteurCP().equals(destinataire) &&
                                                        conv.getDestinataireCP().equals(expediteur);
                                }));
        }

}

package utcapitole.miage.projetdevg3.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import utcapitole.miage.projetdevg3.model.ConversationGrp;
import utcapitole.miage.projetdevg3.model.ConversationPri;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.repository.UtilisateurRepository;
import utcapitole.miage.projetdevg3.service.ConversationService;
import utcapitole.miage.projetdevg3.service.MessageService;
import utcapitole.miage.projetdevg3.service.UtilisateurService;

@WebMvcTest(controllers = CommentaireController.class) // 替换为你的控制器类名

class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConversationService conversationService;

    @MockBean
    private MessageService messageService;

    @MockBean
    private UtilisateurService utilisateurService;

}

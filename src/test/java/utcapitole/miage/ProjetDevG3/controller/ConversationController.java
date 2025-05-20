package utcapitole.miage.projetDevG3.controller;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import utcapitole.miage.projetDevG3.Controller.DemandeAmiController;
import utcapitole.miage.projetDevG3.Service.DemandeAmiService;
import utcapitole.miage.projetDevG3.config.SecurityConfig;
import utcapitole.miage.projetDevG3.model.DemandeAmi;
import utcapitole.miage.projetDevG3.model.StatutDemande;
import utcapitole.miage.projetDevG3.model.Utilisateur;
import utcapitole.miage.projetDevG3.Repository.UtilisateurRepository;

@Controller
@RequiredArgsConstructor
@RequestMapping("/conversations")
public class ConversationController {

    private final UtilisateurRepository utilisateurRepository;
    private final ConversationService conversationService;
    private final MessageRepository messageRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public String afficherConversations(Principal principal, Model model) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(principal.getName())
                .orElseThrow();

        List<ConversationPri> conversations = conversationService.getConversationsOfUser(utilisateur);
        model.addAttribute("conversations", conversations);
        return "conversations"; // 页面展示私聊列表
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public String afficherMessages(@PathVariable Long id, Principal principal, Model model) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(principal.getName())
                .orElseThrow();

        // 验证用户是否属于这个会话
        ConversationPri conversation = conversationPriRepository.findById(id)
                .orElseThrow();

        if (!conversation.getExpediteurCP().equals(utilisateur) &&
                !conversation.getDestinataireCP().equals(utilisateur)) {
            throw new IllegalStateException("Vous n'avez pas accès à cette conversation.");
        }

        List<Message> messages = messageRepository.findByConversationOrderByDtEnvoiAsc(conversation);
        model.addAttribute("messages", messages);
        model.addAttribute("autreUtilisateur", conversation.getExpediteurCP().equals(utilisateur)
                ? conversation.getDestinataireCP()
                : conversation.getExpediteurCP());

        return "messages"; // 显示消息详情页面
    }
}

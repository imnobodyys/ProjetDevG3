package utcapitole.miage.projetDevG3.Controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import utcapitole.miage.projetDevG3.Service.ConversationPriService;
import utcapitole.miage.projetDevG3.Service.ConversationService;
import utcapitole.miage.projetDevG3.Service.DemandeAmiService;
import utcapitole.miage.projetDevG3.model.ConversationPri;
import utcapitole.miage.projetDevG3.model.DemandeAmi;
import utcapitole.miage.projetDevG3.model.Message;
import utcapitole.miage.projetDevG3.model.Utilisateur;
import utcapitole.miage.projetDevG3.Repository.ConversationPriRepository;
import utcapitole.miage.projetDevG3.Repository.MessageRepository;
import utcapitole.miage.projetDevG3.Repository.UtilisateurRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Classe ConversationController
 * Gère les conversations entre utilisateurs
 * 
 * @author [Votre nom]
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/conversations")
public class ConversationController {

    private final UtilisateurRepository utilisateurRepository;
    private final ConversationService conversationService;
    private final MessageRepository messageRepository;
    private final ConversationPriRepository conversationPriRepository;

    /**
     * voir touts les conversation lien avec utilisateur
     * 
     * @param principal
     * @param model
     * @return page
     */
    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public String afficherConversations(Principal principal, Model model) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(principal.getName())
                .orElseThrow();

        List<ConversationPri> conversations = conversationService.getConversationsOfUser(utilisateur);
        model.addAttribute("conversations", conversations);
        return "conversation";
    }

    /**
     * pour voir message de conversation
     * 
     * @param id
     * @param principal
     * @param model
     * @returnpage
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public String afficherMessages(@PathVariable Long id, Principal principal, Model model) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(principal.getName())
                .orElseThrow();

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

        return "messages";
    }
}

package utcapitole.miage.projetdevg3.controller;

import java.security.Principal;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import utcapitole.miage.projetdevg3.model.ConversationGrp;
import utcapitole.miage.projetdevg3.model.ConversationPri;

import utcapitole.miage.projetdevg3.model.Message;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.service.ConversationService;
import utcapitole.miage.projetdevg3.repository.UtilisateurRepository;

/**
 * Classe ConversationController
 * Gère les conversations entre utilisateurs
 * 
 * 
 */
@Controller
@RequestMapping("/conversations")
public class ConversationController {

        private final UtilisateurRepository utilisateurRepository;
        private final ConversationService conversationService;

        public ConversationController(ConversationService conversationService,
                        UtilisateurRepository utilisateurRepository) {
                this.conversationService = conversationService;
                this.utilisateurRepository = utilisateurRepository;
        }

        /**
         * Récupère l'utilisateur courant depuis Principal
         */
        private Utilisateur getCurrentUser(Principal principal) {
                return utilisateurRepository.findByEmail(principal.getName())
                                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
        }

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
                Utilisateur utilisateur = getCurrentUser(principal);

                List<ConversationPri> privateConvs = conversationService.getConversationsOfUser(utilisateur);
                List<ConversationGrp> groupConvs = conversationService.getGroupConversationsOfUser(utilisateur);
                List<Message> recentPrivate = conversationService.getRecentPrivateMessages(utilisateur);
                List<Message> recentGroup = conversationService.getRecentGroupMessages(utilisateur);

                model.addAttribute("privateConvs", privateConvs);
                model.addAttribute("groupConvs", groupConvs);
                model.addAttribute("recentPrivate", recentPrivate);
                model.addAttribute("recentGroup", recentGroup);

                return "conversation";

        }
}

package utcapitole.miage.projetdevg3.controller;

import java.security.Principal;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import utcapitole.miage.projetdevg3.model.ConversationPri;

import utcapitole.miage.projetdevg3.model.Message;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.service.ConversationService;
import utcapitole.miage.projetdevg3.repository.UtilisateurRepository;

import utcapitole.miage.projetdevg3.repository.UtilisateurRepository;

/**
 * Classe ConversationController
 * Gère les conversations entre utilisateurs
 * 
 * 
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/conversations")
public class ConversationController {

        private final UtilisateurRepository utilisateurRepository;
        private final ConversationService conversationService;

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
                Utilisateur utilisateur = getCurrentUser(principal);
                List<Message> messages = conversationService.getMessagesForConversation(id, utilisateur);
                Utilisateur autreUtilisateur = conversationService.getOtherUser(id, utilisateur);

                model.addAttribute("messages", messages);
                model.addAttribute("autreUtilisateur", autreUtilisateur);

                return "messages";
        }
}

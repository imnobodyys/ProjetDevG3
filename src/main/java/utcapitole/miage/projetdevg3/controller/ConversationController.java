
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
import utcapitole.miage.projetdevg3.service.MessageService;
import utcapitole.miage.projetdevg3.repository.UtilisateurRepository;

@Controller
@RequestMapping("/conversations")
@RequiredArgsConstructor
public class ConversationController {

        private final UtilisateurRepository utilisateurRepository;
        private final ConversationService conversationService;
        private final MessageService messageService;

        /**
         * Affiche le centre de messages avec toutes les conversations
         */
        @GetMapping
        @PreAuthorize("isAuthenticated()")
        public String afficherCentreMessages(Principal principal, Model model) {
                Utilisateur utilisateur = getCurrentUser(principal);

                // Récupère les conversations avec les derniers messages inclus
                List<ConversationPri> privateConvs = conversationService
                                .getConversationsWithRecentMessages(utilisateur);
                List<ConversationGrp> groupConvs = conversationService
                                .getGroupConversationsWithRecentMessages(utilisateur);

                model.addAttribute("privateConvs", privateConvs);
                model.addAttribute("groupConvs", groupConvs);

                return "conversation"; // Correspond au nouveau template HTML
        }

        /**
         * Envoyer un message privé
         */
        @PostMapping("/privee/{conversationId}")
        @PreAuthorize("isAuthenticated()")
        public String envoyerMessagePrive(
                        @PathVariable Long conversationId,
                        @RequestParam String contenu,
                        Principal principal) {
                Utilisateur expediteur = getCurrentUser(principal);

                messageService.envoyerMessagePrive(conversationId, expediteur, contenu);

                return "redirect:/conversations#" + conversationId;
        }

        /**
         * Envoyer un message de groupe
         */
        @PostMapping("/groupe/{conversationId}")
        @PreAuthorize("isAuthenticated()")
        public String envoyerMessageGroupe(
                        @PathVariable Long conversationId,
                        @RequestParam String contenu,
                        Principal principal) {
                Utilisateur expediteur = getCurrentUser(principal);

                messageService.envoyerMessageGroupe(conversationId, expediteur, contenu);

                return "redirect:/conversations#groupe-" + conversationId;
        }

        private Utilisateur getCurrentUser(Principal principal) {
                return utilisateurRepository.findByEmail(principal.getName())
                                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
        }

}

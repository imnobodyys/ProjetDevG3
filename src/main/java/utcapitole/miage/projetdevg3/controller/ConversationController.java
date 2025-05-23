
package utcapitole.miage.projetdevg3.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                                .getPrivateConversationsWithRecentMessages(utilisateur);
                List<ConversationGrp> groupConvs = conversationService
                                .getGroupConversationsWithRecentMessages(utilisateur);

                Map<Long, Utilisateur> autres = new HashMap<>();
                for (ConversationPri conv : privateConvs) {
                        Utilisateur autre = conversationService.getOtherUser(conv.getId(), utilisateur);
                        autres.put(conv.getId(), autre);
                }
                model.addAttribute("privateConvs", privateConvs);
                model.addAttribute("groupConvs", groupConvs);
                model.addAttribute("autreUtilisateurs", autres);

                return "conversation"; // Correspond au nouveau template HTML
        }

        private Utilisateur getCurrentUser(Principal principal) {
                return utilisateurRepository.findByEmail(principal.getName())
                                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
        }

}

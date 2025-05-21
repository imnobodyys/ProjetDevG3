package utcapitole.miage.projetdevg3.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import utcapitole.miage.projetdevg3.service.MessageService;
import utcapitole.miage.projetdevg3.service.UtilisateurService;
import utcapitole.miage.projetdevg3.model.Message;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.repository.UtilisateurRepository;

/**
 * Classe MessageController
 * Gère les messages entre utilisateurs
 * 
 * @author [Votre nom]
 */
@Controller
@RequestMapping("/messages")
public class MessageController {

    private final UtilisateurService utilisateurService;
    private final UtilisateurRepository utilisateurRepository;
    private final MessageService messageService;

    public MessageController(UtilisateurService utilisateurService, MessageService messageService,
            UtilisateurRepository utilisateurRepository) {
        this.utilisateurService = utilisateurService;
        this.messageService = messageService;
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * Formulaire pour envoyer un message privé
     */
    @GetMapping("/envoyer/{destinataireId}")
    @PreAuthorize("isAuthenticated()")
    public String formulaireMessage(@PathVariable Long destinataireId, Model model) {
        model.addAttribute("destinataireId", destinataireId);
        return "form-message";
    }

    /**
     * Traitement d’envoi de message
     */
    @PostMapping("/envoyer")
    @PreAuthorize("isAuthenticated()")
    public String envoyerMessage(@RequestParam Long destinataireId,
            @RequestParam String contenu,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        Utilisateur expediteur = utilisateurRepository.findByEmail(principal.getName())
                .orElseThrow();
        Utilisateur destinataire = utilisateurRepository.findById(destinataireId)
                .orElseThrow();

        messageService.envoyerMessage(expediteur, destinataire, contenu);

        redirectAttributes.addFlashAttribute("success", "Message envoyé !");
        return "redirect:/demandes/amis";
    }

    /**
     * pour arriver page de list message
     * 
     * @param model
     * @param principal
     * @return
     */
    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public String afficherMessagesAccueil(Model model, Principal principal) {
        Utilisateur utilisateur = utilisateurService.getUtilisateurByEmail(principal.getName());

        List<Message> recentPrivateMessages = messageService.getRecentPriMessages(utilisateur);
        List<Message> recentGroupMessages = messageService.getRecentGroupMessages(utilisateur);

        model.addAttribute("privateMessages", recentPrivateMessages);
        model.addAttribute("groupMessages", recentGroupMessages);
        return "messages";
    }
}

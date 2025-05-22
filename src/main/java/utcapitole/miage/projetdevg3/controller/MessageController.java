package utcapitole.miage.projetdevg3.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import utcapitole.miage.projetdevg3.service.ConversationService;
import utcapitole.miage.projetdevg3.service.MessageService;
import utcapitole.miage.projetdevg3.service.UtilisateurService;
import utcapitole.miage.projetdevg3.model.ConversationPri;
import utcapitole.miage.projetdevg3.model.Message;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.repository.ConversationPriRepository;
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
    private final ConversationPriRepository conversationPriRepository;

    public MessageController(UtilisateurService utilisateurService, MessageService messageService,
            UtilisateurRepository utilisateurRepository, ConversationPriRepository ConversationPriRepository) {
        this.utilisateurService = utilisateurService;
        this.messageService = messageService;
        this.utilisateurRepository = utilisateurRepository;
        this.conversationPriRepository = ConversationPriRepository;
    }

    /**
     * Formulaire pour envoyer un message privé
     */
    @GetMapping("/envoyer/{id}")
    @PreAuthorize("isAuthenticated()")
    public String afficherFormulaireMessage(@PathVariable Long id, Model model) {
        Utilisateur destinataire = utilisateurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        model.addAttribute("destinataire", destinataire);
        return "form-message";
    }

    /**
     * Traitement d’envoi de message
     */
    @PostMapping("/envoyer")
    @PreAuthorize("isAuthenticated()")
    public String envoyerMessagePri(@RequestParam Long destinataireId,
            @RequestParam String contenu,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        Utilisateur expediteur = utilisateurRepository.findByEmail(principal.getName())
                .orElseThrow();
        Utilisateur destinataire = utilisateurRepository.findById(destinataireId)
                .orElseThrow();

        messageService.envoyerOuCreerMessagePrive(expediteur, destinataire, contenu);

        return "redirect:/messages/list/privee/" + destinataireId;
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

        List<Message> recentPrivateMessages = messageService.getRecentPrivateMessages(utilisateur);
        List<Message> recentGroupMessages = messageService.getRecentGroupMessages(utilisateur);

        model.addAttribute("privateMessages", recentPrivateMessages);
        model.addAttribute("groupMessages", recentGroupMessages);
        return "messages";
    }

    /**
     * pour avoir page de list message de un ami
     * 
     * @param utilisateurId
     * @param principal
     * @param model
     * @return
     */
    @GetMapping("/list/privee/{utilisateurId}")
    @PreAuthorize("isAuthenticated()")
    public String afficherMessagesAvecUtilisateur(@PathVariable Long utilisateurId, Principal principal, Model model) {
        Utilisateur moi = utilisateurRepository.findByEmail(principal.getName())
                .orElseThrow();
        Utilisateur autre = utilisateurRepository.findById(utilisateurId)
                .orElseThrow();

        ConversationPri conversation = conversationPriRepository
                .findConversationBetween(
                        moi, autre)
                .orElseThrow(() -> new IllegalArgumentException("Pas de conversation trouvée."));

        List<Message> messages = messageService.getMessagesByConversationId(conversation.getId());

        model.addAttribute("currentUserId", moi.getId());
        model.addAttribute("messages", messages);
        model.addAttribute("autre", autre); // pour affichage
        return "messages";
    }

    /**
     * envoyer mesasage au group
     * 
     * @param groupeId
     * @param contenu
     * @param principal
     * @return
     */
    @PostMapping("/{groupeId}/messages")
    public String envoyerMessageGrp(@PathVariable Long groupeId,
            @RequestParam String contenu,
            Principal principal) {
        Utilisateur expediteur = utilisateurRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        messageService.envoyerMessageGroupe(groupeId, expediteur, contenu);
        return "redirect:/groupes/" + groupeId + "/messages";
    }
}

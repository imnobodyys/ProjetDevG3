package utcapitole.miage.projetdevg3.controller;

import java.security.Principal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.service.DemandeAmiService;
import utcapitole.miage.projetdevg3.repository.UtilisateurRepository;

/**
 * classe DemandeAmiController
 * Gère les demandes d'amis entre utilisateurs
 * 
 *
 */
@Controller
@RequestMapping("/demandes")
@EnableMethodSecurity
public class DemandeAmiController {

    public DemandeAmiController(utcapitole.miage.projetdevg3.service.DemandeAmiService demandeAmiService,
            utcapitole.miage.projetdevg3.repository.UtilisateurRepository utilisateurRepository) {
        this.demandeAmiService = demandeAmiService;
        this.utilisateurRepository = utilisateurRepository;
    }

    private final DemandeAmiService demandeAmiService;
    private final UtilisateurRepository utilisateurRepository;

    /**
     * Récupère l'utilisateur courant depuis Principal
     */
    private Utilisateur getCurrentUser(Principal principal) {
        return utilisateurRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
    }

    /**
     * Formulaire d'envoi de demande
     * 
     * @param destinataireId
     * @param model
     * @return page de demande
     * 
     */
    @GetMapping("/form/{destinataireId}")
    @PreAuthorize("isAuthenticated()")
    public String showDemandeForm(@PathVariable Long destinataireId, Model model) {
        model.addAttribute("destinataireId", destinataireId);
        return "form_demande";
    }

    /**
     * Envoi d'une demande d'ami
     * 
     * @param destinataireId
     * @param principal
     * @param redirectAttributes
     * @return page utilisateur
     */
    @PostMapping("/envoyer")
    @PreAuthorize("isAuthenticated()")
    public String envoyerDemande(
            @RequestParam Long destinataireId,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        Utilisateur currentUser = getCurrentUser(principal);
        demandeAmiService.envoyerDemandeAmi(currentUser.getId(), destinataireId);

        redirectAttributes.addFlashAttribute("success", "Demande envoyée avec succès !");
        return "redirect:/accueil";
    }

    /**
     * Liste des demandes reçues en attente
     * 
     * @param principal
     * @param model
     * @return
     */
    @GetMapping("/recues")
    @PreAuthorize("isAuthenticated()")
    public String getDemandesRecues(Principal principal, Model model) {
        Utilisateur currentUser = getCurrentUser(principal);
        model.addAttribute("demandes", demandeAmiService.getDemandesRecuesEnAttente(currentUser));
        return "demandes-recues";
    }

    /**
     * Accepter une demande
     * 
     * @param id
     * @param principal
     * @return
     */
    @PostMapping("/accepter/{id}")
    @PreAuthorize("isAuthenticated()")
    public String accepterDemande(
            @PathVariable Long id,
            Principal principal) {

        Utilisateur currentUser = getCurrentUser(principal);
        demandeAmiService.accepterDemande(id, currentUser);
        return "redirect:/demandes/recues";
    }

    /**
     * Refuser une demande
     * 
     * @param id
     * @param principal
     * @return
     */
    @PostMapping("/refuser/{id}")
    @PreAuthorize("isAuthenticated()")
    public String refuserDemande(
            @PathVariable Long id,
            Principal principal) {

        Utilisateur currentUser = getCurrentUser(principal);
        demandeAmiService.refuserDemande(id, currentUser);
        return "redirect:/demandes/recues";
    }

    /**
     * Liste des amis
     * 
     * @param principal
     * @param model
     * @return
     */
    @GetMapping("/amis")
    @PreAuthorize("isAuthenticated()")
    public String voirMesAmis(Principal principal, Model model) {
        Utilisateur currentUser = getCurrentUser(principal);
        model.addAttribute("amis", demandeAmiService.getAmis(currentUser));
        model.addAttribute("utilisateur", currentUser);
        return "amis";
    }

    /**
     * Supprimer un ami
     * 
     * @param id
     * @param principal
     * @return
     */
    @PostMapping("/supprimer/{id}")
    @PreAuthorize("isAuthenticated()")
    public String supprimerAmi(
            @PathVariable Long id,
            Principal principal) {

        Utilisateur currentUser = getCurrentUser(principal);
        demandeAmiService.supprimerAmi(currentUser, id);
        return "redirect:/demandes/amis";
    }

}

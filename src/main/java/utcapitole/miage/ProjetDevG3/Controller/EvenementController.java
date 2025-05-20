package utcapitole.miage.projetdevg3.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import utcapitole.miage.projetdevg3.model.Evenement;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.service.EvenementService;
import utcapitole.miage.projetdevg3.service.UtilisateurService;

/**
 * classe EvenementController
 * Gère les événements de l'application
 */
@Controller
@RequestMapping("/api/evenements")
public class EvenementController {

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private EvenementService evenementService;

    /**
     * US43 - Création d'événement
     * Affiche le formulaire de création d'événement
     * 
     * @param model Conteneur des attributs pour la vue
     * @param token Jeton CSRF pour la protection du formulaire
     * @return Nom de la vue Thymeleaf
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/creer")
    public String afficherFormulaireEvenement(Model model, CsrfToken token) {
        model.addAttribute("evenement", new Evenement());
        model.addAttribute("_csrf", token);
        return "creerEvenement";
    }

    /**
     * US43 - Création d'événement
     * 
     * @param evenement      Données du formulaire
     * @param authentication Informations d'authentification
     * @param model          Conteneur des attributs
     * @return Page de confirmation ou formulaire avec erreur
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/creer")
    public String creerEvenement(@ModelAttribute("evenement") Evenement evenement,
            Authentication authentication,
            Model model) {
        try {
            Utilisateur currentUser = utilisateurService.getUtilisateurByEmail(authentication.getName());
            evenement.setAuteur(currentUser);
            evenement.setDatePublication(LocalDateTime.now());

            evenementService.creerEvenement(evenement);
            model.addAttribute("evenement", evenement);
            return "confirmationEvenement";
        } catch (IllegalArgumentException e) {
            model.addAttribute("message", "Erreur : " + e.getMessage());
            return "creerEvenement";
        }
    }

    /**
     * US44 Modifier un événement
     * Affiche le formulaire de modification
     * Vérifie que l'utilisateur est l'auteur de l'événement avant d'autoriser
     * l'accès
     * 
     * @param id             ID de l'événement à modifier
     * @param model          Conteneur des attributs
     * @param authentication Informations d'authentification
     * @return Vue de modification ou redirection en cas d'erreur
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modifier/{id}")
    public String afficherFormulaireModification(
            @PathVariable Long id,
            Model model,
            Authentication authentication) {
        Utilisateur currentUser = utilisateurService.getUtilisateurByEmail(authentication.getName());

        Evenement evenement = evenementService.getEvenementById(id);

        if (!evenement.getAuteur().getId().equals(currentUser.getId())) {
            model.addAttribute("errorMessage", "Accès refusé : Vous n'êtes pas l'auteur de cet événement");
            return "errorPage";
        }

        model.addAttribute("evenement", evenement);
        return "modifierEvenement";
    }

    /**
     * US44 Modifier un événement
     * Traite la soumission du formulaire
     * 
     * @param id             ID de l'événement
     * @param evenement      Données mises à jour
     * @param authentication Informations d'authentification
     * @param model          Conteneur des attributs
     * @return Page de confirmation ou formulaire avec erreur
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modifier/{id}")
    public String soumettreModification(@PathVariable Long id,
            @ModelAttribute("evenement") Evenement evenement,
            Authentication authentication,
            Model model) {
        try {
            Utilisateur currentUser = utilisateurService.getUtilisateurByEmail(authentication.getName());
            Evenement updatedEvent = evenementService.modifierEvenement(id, evenement, currentUser);
            model.addAttribute("evenement", updatedEvent);
            return "confirmationEvenement";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "modifierEvenement";
        }
    }

    /**
     * US45 - Suppression d'un événement
     * Supprime un événement existant après vérification des droits
     * 
     * @param id             ID de l'événement à supprimer
     * @param authentication Informations d'authentification
     * @param model          Conteneur des attributs
     * @return Redirection ou page d'erreur
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/supprimer/{id}")
    public String supprimerEvenement(@PathVariable Long id,
            Authentication authentication,
            Model model) {
        try {
            Utilisateur currenyUser = utilisateurService.getUtilisateurByEmail(authentication.getName());
            evenementService.supprimerEvenement(id, currenyUser);
            return "confirmationSuppression";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "errorPage";
        }
    }

    

    /**
     * US47 - Participer à un événement
     * Permet à un utilisateur authentifié de s'inscrire à un événement
     * 
     * @param id ID de l'événement
     * @param authentication Informations d'authentification
     * @param model Conteneur des attributs
     * @return Redirection vers la page de confirmation ou d'erreur
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/participer/{id}")
    public String participerEvenement(@PathVariable Long id,
                                    Authentication authentication,
                                    Model model){
        try{
            Utilisateur participant = utilisateurService.getUtilisateurByEmail(authentication.getName());
            Evenement evenement = evenementService.participerEvenement(id, participant);

            model.addAttribute("evenement", evenement);
            return "confirmationParticipation";
        } catch (IllegalArgumentException e){
            model.addAttribute("errorMessage", e.getMessage());
            return "errorPage";
        }
    }
}

package utcapitole.miage.projetDevG3.Controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import utcapitole.miage.projetDevG3.Repository.EvenementRepository;
import utcapitole.miage.projetDevG3.Repository.UtilisateurRepository;
import utcapitole.miage.projetDevG3.Service.EvenementService;
import utcapitole.miage.projetDevG3.Service.UtilisateurService;
import utcapitole.miage.projetDevG3.model.Evenement;
import utcapitole.miage.projetDevG3.model.Utilisateur;

/** classe EvenementController
 * Gère les événements de l'application
 */
@Controller
@RequestMapping("/api/evenements")
public class EvenementController {

    @Autowired
    private UtilisateurService utilisateurService;

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
     * @param evenement Données du formulaire
     * @param authentication Informations d'authentification
     * @param model Conteneur des attributs
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
}

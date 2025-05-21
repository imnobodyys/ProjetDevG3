package utcapitole.miage.projetdevg3.controller;

import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.service.UtilisateurService;

@Controller

public class AccueilController {

    private final UtilisateurService utilisateurService;

    public AccueilController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    /**
     * Affiche la page d'accueil avec des contenus différenciés selon l'état
     * d'authentification
     * 
     * @param model     Pour transmettre des attributs à la vue
     * @param principal Objet Spring Security contenant les infos de l'utilisateur
     *                  connecté
     * @return Le nom du template Thymeleaf
     */
    @GetMapping("/accueil")
    public String afficherAccueil(Model model, Principal principal) {
        if (principal != null) {
            Utilisateur utilisateur = utilisateurService.getUtilisateurByEmail(principal.getName());
            model.addAttribute("utilisateur", utilisateur);
            model.addAttribute("isAuthenticated", true);
        } else {
            model.addAttribute("isAuthenticated", false);
        }
        return "accueil";
    }
    @GetMapping("/accueil")
    public String accueil() {
        return "redirect:/api/utilisateurs/login";  // Nom de la vue Thymeleaf accueil.html
    }

    public UtilisateurService getUtilisateurService() {
        return utilisateurService;
    }
}

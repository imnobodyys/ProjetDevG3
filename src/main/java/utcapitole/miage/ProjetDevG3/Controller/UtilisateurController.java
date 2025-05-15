package utcapitole.miage.projetDevG3.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import utcapitole.miage.projetDevG3.Service.UtilisateurService;
import utcapitole.miage.projetDevG3.model.Utilisateur;

@Controller
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;

    /**
     * US01 - Création de profil personnel
     * Affiche le formulaire d'inscription
     * @param model Conteneur des attributs pour la vue
     * @param token Jeton CSRF pour la protection du formulaire
     * @return Nom de la vue Thymeleaf
     */
    @GetMapping("/creer")
    public String afficherFormulaire(Model model, CsrfToken token) {
        model.addAttribute("utilisateur", new Utilisateur());
        model.addAttribute("_csrf", token);
        return "creerUtilisateur"; 
    }

    /**
     * US01 - Création de profil personnel
     * @param utilisateur données du formulaire
     * @param model pour passer l'utilisateur à la vue confirmation
     * @return page de confirmation ou formulaire avec message d'erreur
     */
    @PostMapping("/creer")
    public String creerUtilisateur(@ModelAttribute("utilisateur") Utilisateur utilisateur, Model model) {
        try {
            Utilisateur utilisateurCree = utilisateurService.creerUtilisateur(utilisateur);
            model.addAttribute("utilisateur", utilisateurCree);
            return "confirmationUtilisateur"; // page de confirmation
        } catch (IllegalArgumentException e) {
            model.addAttribute("message", "Erreur : " + e.getMessage());
            return "creerUtilisateur"; // retourne au formulaire
        }
    }
}

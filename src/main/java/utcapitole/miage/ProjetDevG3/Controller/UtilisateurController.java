package utcapitole.miage.projetDevG3.Controller;

/**
 * Classe MessageController
 * Gère les messages entre utilisateurs
 * @author [Votre nom]
 */
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import utcapitole.miage.projetDevG3.Repository.UtilisateurRepository;
import utcapitole.miage.projetDevG3.Service.UtilisateurService;
import utcapitole.miage.projetDevG3.model.Utilisateur;

@Controller
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private UtilisateurRepository utilisateurRepository;
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

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/search")
    public String searchUtilisateurs(@RequestParam(value = "q", required = false) String keyword, Model model) {
        List<Utilisateur> resultats = null;

        // si le utilisateur saisi le keword on commence recherche
        if (keyword != null && !keyword.isEmpty()) {
            resultats = utilisateurService.rechercher(keyword);
        }

        // transmis key word et resultat
        model.addAttribute("keyword", keyword);
        model.addAttribute("resultats", resultats);

        // rentrer page search
        return "search";
    }

    @GetMapping("/init")
    @ResponseBody
    public String initData() {
        Utilisateur u1 = new Utilisateur("Alice", "Dupont", "alice@example.com", "password");
        Utilisateur u2 = new Utilisateur("Bob", "Martin", "bob@example.com", "123456");
        utilisateurRepository.save(u1);
        utilisateurRepository.save(u2);
        return "OK";
    }
}

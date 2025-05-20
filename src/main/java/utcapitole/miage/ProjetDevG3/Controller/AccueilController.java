package utcapitole.miage.projetDevG3.Controller;

import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccueilController {

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
    public String accueil(Model model, Principal principal) {
        boolean isAuthenticated = (principal != null);
        model.addAttribute("isAuthenticated", isAuthenticated);

        if (isAuthenticated) {
            model.addAttribute("userEmail", principal.getName());
            // Ajoutez ici d'autres attributs nécessaires pour les utilisateurs connectés
        }

        return "accueil";
    }
}
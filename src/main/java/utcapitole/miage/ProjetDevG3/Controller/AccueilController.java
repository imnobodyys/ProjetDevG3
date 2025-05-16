package utcapitole.miage.projetDevG3.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
/** Classe AccueilController
 * Gère la page d'accueil de l'application
 * @author [Votre nom]
 */
@Controller
public class AccueilController {
    /*
     * Méthode d'accueil
     * @return la vue d'accueil
     */
    @GetMapping("/")
    public String accueil() {
        return "accueil"; // Renvoie la vue d'accueil
    }

}

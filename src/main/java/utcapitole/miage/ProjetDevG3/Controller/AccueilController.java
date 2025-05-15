package utcapitole.miage.projetDevG3.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/*controller pour accueilcomtroller */
@Controller
public class AccueilController {
    @GetMapping("/")
    public String accueil() {
        return "accueil"; // Renvoie la vue d'accueil
    }

}

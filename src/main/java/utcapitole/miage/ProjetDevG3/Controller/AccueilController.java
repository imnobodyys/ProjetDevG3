package utcapitole.miage.projetDevG3.controller;

import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccueilController {

     @GetMapping("/login-accueil")
    public String loginAccueil() {
        return "login"; // affiche login.html, page de connexion
    }
    @GetMapping("/accueil")
    public String accueil() {
        return "redirect:/api/utilisateurs/login";  // Nom de la vue Thymeleaf accueil.html
    }
}

package utcapitole.miage.projetDevG3.Controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
     @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/api/utilisateurs/login"; // sécurité basique côté contrôleur
        }

        String email = principal.getName();
        model.addAttribute("userEmail", email);

        return "dashboard"; 
    }

}

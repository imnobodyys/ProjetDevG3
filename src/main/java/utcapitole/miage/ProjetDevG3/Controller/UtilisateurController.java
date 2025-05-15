package utcapitole.miage.projetDevG3.Controller;

import java.util.List;
import org.springframework.ui.Model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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

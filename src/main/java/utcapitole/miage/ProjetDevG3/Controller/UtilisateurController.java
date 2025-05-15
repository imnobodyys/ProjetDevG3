package utcapitole.miage.projetDevG3.Controller;

import java.util.List;
import org.springframework.ui.Model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import utcapitole.miage.projetDevG3.Service.UtilisateurService;
import utcapitole.miage.projetDevG3.model.Utilisateur;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;

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
}

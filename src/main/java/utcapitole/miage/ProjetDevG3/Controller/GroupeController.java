package utcapitole.miage.projetDevG3.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import utcapitole.miage.projetDevG3.Repository.GroupeRepository;
import utcapitole.miage.projetDevG3.model.Groupe;
import utcapitole.miage.projetDevG3.model.Utilisateur;

@Controller
@RequestMapping("/groupes")
public class GroupeController {
    @Autowired
    private final GroupeRepository groupeRepository;
    // Constructeur pour injecter le repository
    public GroupeController(GroupeRepository groupeRepository) {
        this.groupeRepository = groupeRepository;
    }
    @PostMapping("/creer")
    public String creerGroupe(@RequestParam String nom, @RequestParam String description, HttpSession session)  {
        // Récupérer l'utilisateur connecté depuis la session
        Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("utilisateurConnecte");
        // Créer un nouveau groupe
        Groupe groupe = new Groupe();
        groupe.setNom(nom);
        groupe.setDescription(description);
        groupe.setCreateur(utilisateurConnecte);
        // Enregistrer le groupe dans la base de données
        groupeRepository.save(groupe);
        return "redirect:/groupes/liste"; // Rediriger vers la liste des groupes après la création
    }
    

}

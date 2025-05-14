package utcapitole.miage.projetDevG3.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import utcapitole.miage.projetDevG3.Repository.GroupeRepository;
import utcapitole.miage.projetDevG3.model.Groupe;
import utcapitole.miage.projetDevG3.model.Utilisateur;

@Controller
@RequestMapping("/groupe")
public class GroupeController {

    private final GroupeRepository groupeRepository;
    public GroupeController(GroupeRepository groupeRepository) {
        this.groupeRepository = groupeRepository;
    }
    @PostMapping("/creer")
     public ResponseEntity<Groupe> creerGroupe(
            @RequestParam String nom,
          


    


}

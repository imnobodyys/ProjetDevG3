package utcapitole.miage.projetDevG3.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import utcapitole.miage.projetDevG3.Service.UtilisateurService;
import utcapitole.miage.projetDevG3.model.Utilisateur;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;

    /**
     * US01
     * Création de profile personnel
     * @param utilisateur objet JSON envoyé dans la requête
     * @return utilisateur créé ou message d'erreur
     */
    @PostMapping("/creer")
    public ResponseEntity<?> creerUtilisateur(@RequestBody Utilisateur utilisateur) {
        try {
            Utilisateur utilisateurCree = utilisateurService.creerUtilisateur(utilisateur);
            return new ResponseEntity<>(utilisateurCree, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}

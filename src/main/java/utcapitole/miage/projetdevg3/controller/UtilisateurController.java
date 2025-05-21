package utcapitole.miage.projetdevg3.controller;

import java.security.Principal;
import java.util.ArrayList;
/**
 * Classe MessageController
 * Gère les messages entre utilisateurs
 * @author projetdevg3
 */
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import utcapitole.miage.projetdevg3.service.UtilisateurService;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.repository.UtilisateurRepository;

@Controller
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    /**
     * US01 - Création de profil personnel
     * Affiche le formulaire d'inscription
     * 
     * @param model Conteneur des attributs pour la vue
     * @param token Jeton CSRF pour la protection du formulaire
     * @return Nom de la vue Thymeleaf
     */
    @GetMapping("/creer")
    public String afficherFormulaire(Model model, CsrfToken token) {
        model.addAttribute("utilisateur", new Utilisateur());
        model.addAttribute("_csrf", token);
        return "creerUtilisateur";
    }

    /**
     * US01 - Création de profil personnel
     * 
     * @param utilisateur données du formulaire
     * @param model       pour passer l'utilisateur à la vue confirmation
     * @return page de confirmation ou formulaire avec message d'erreur
     */
    @PostMapping("/creer")
    public String creerUtilisateur(@ModelAttribute("utilisateur") Utilisateur utilisateur, Model model) {
        try {
            Utilisateur utilisateurCree = utilisateurService.creerUtilisateur(utilisateur);
            model.addAttribute("utilisateur", utilisateurCree);
            return "confirmationUtilisateur"; // page de confirmation
        } catch (IllegalArgumentException e) {
            model.addAttribute("message", "Erreur : " + e.getMessage());
            return "creerUtilisateur"; // retourne au formulaire
        }
    }

    /**
     * US02 - Connexion à mon compte
     * 
     * @param error Paramètre optionnel indiquant une erreur d'authentification
     * @param model Conteneur pour les attributs de la vue
     * @return Nom de la vue Thymeleaf
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
            Model model, CsrfToken csrfToken) {
        if (error != null) {
            model.addAttribute("errorMessage", "Identifiants incorrects");
        }
        model.addAttribute("_csrf", csrfToken); // <-- Ajout ici

        return "login";
    }

    /**
     * US03 - Modification de profil
     * Affiche le formulaire de modification du profil pour l'utilisateur connecté.
     * 
     * @param authentication Objet contenant les informations d'authentification de
     *                       l'utilisateur courant
     * @param model          Conteneur des attributs pour la vue
     * @return Nom de la vue Thymeleaf affichant le formulaire de modification
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modifier")
    public String afficherFormulaireModification(Authentication authentication, Model model) {
        Utilisateur utilisateur = utilisateurService.getUtilisateurByEmail(authentication.getName());
        model.addAttribute("utilisateur", utilisateur);
        return "modifierProfil";
    }

    /**
     * US03 - Modification de profil
     * Soumet les modifications apportées au profil de l'utilisateur connecté.
     *
     * @param utilisateur    Objet contenant les nouvelles informations du profil
     * @param authentication Informations d'authentification de l'utilisateur
     *                       connecté
     * @param model          Conteneur des attributs pour la vue
     * @return Nom de la vue Thymeleaf à afficher (confirmation ou formulaire avec
     *         erreur)
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modifier")
    public String soumettreModification(@ModelAttribute("utilisateur") Utilisateur utilisateur,
            Authentication authentication,
            Model model) {

        try {
            Utilisateur currentUser = utilisateurService.getUtilisateurByEmail(authentication.getName());
            Utilisateur updatedUser = utilisateurService.modifierUtilisateur(currentUser.getId(), utilisateur);
            model.addAttribute("utilisateur", updatedUser);
            return "confirmationUtilisateur";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "modifierProfil";
        }
    }

    /**
     * US04 - Suppression de profil
     * Supprime le compte de l'utilisateur connecté, puis redirige vers la page de
     * connexion.
     * 
     * @param authentication Objet contenant les informations d'authentification de
     *                       l'utilisateur courant
     * @param model          Conteneur des attributs pour la vue
     * @return Redirection vers la page de connexion avec le paramètre ?logout
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/supprimer")
    public String supprimerProfil(Authentication authentication, Model model) {
        Utilisateur utilisateur = utilisateurService.getUtilisateurByEmail(authentication.getName());
        utilisateurService.supprimerUtilisateur(utilisateur.getId());
        model.addAttribute("message", "Votre profil a été supprimé avec succès");
        return "redirect:/api/utilisateurs/login?logout";
    }

    /**
     * pour search utilisateur
     * 
     * @param keyword
     * @param model
     * @param principal
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/search")
    public String searchUtilisateurs(@RequestParam(value = "q", required = false) String keyword,
            Model model,
            Principal principal) {
        List<Utilisateur> resultats = new ArrayList<>();

        Utilisateur utilisateur = utilisateurService.getUtilisateurByEmail(principal.getName());

        if (keyword != null && !keyword.isEmpty()) {
            resultats = utilisateurService.rechercher(keyword).stream()
                    .filter(u -> !u.getId().equals(utilisateur.getId()))
                    .toList();
        }
        List<Utilisateur> amis = utilisateurService.listerAmis(utilisateur.getId());
        Set<Long> amisIds = amis.stream()
                .map(Utilisateur::getId)
                .collect(Collectors.toSet());

        model.addAttribute("utilisateur", utilisateur);
        model.addAttribute("keyword", keyword);
        model.addAttribute("resultats", resultats);
        model.addAttribute("amisIds", amisIds);

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

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profil/{id}")
    public String afficherProfil(@PathVariable Long id, Model model) {
        Utilisateur utilisateur = utilisateurService.trouverParId(id);
        if (utilisateur == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé");
        }
        model.addAttribute("utilisateur", utilisateur);
        return "profil";
    }
}

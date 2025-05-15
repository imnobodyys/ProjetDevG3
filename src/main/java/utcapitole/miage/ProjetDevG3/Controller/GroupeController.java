package utcapitole.miage.projetDevG3.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import utcapitole.miage.projetDevG3.Service.GroupeService;
import utcapitole.miage.projetDevG3.model.Groupe;
import utcapitole.miage.projetDevG3.model.MembreGroupe;
import utcapitole.miage.projetDevG3.model.StatutMembre;
import utcapitole.miage.projetDevG3.model.Utilisateur;

@Controller
@RequestMapping("/groupes")
public class GroupeController {

    @Autowired
    private GroupeService groupeService;
    // Constructeur pour injecter le service
    public GroupeController(GroupeService groupeService) {
        this.groupeService = groupeService;   
    }
    // Méthode pour afficher le formulaire de création de groupe
    @GetMapping("/creer")
    public String afficherFormulaire(Model model) {
        model.addAttribute("groupe", new Groupe());// Ajoute un objet vide au modèle
        return "formulaireGroupe"; // Va chercher formulaireGroupe.html
    }

    // Envoie du formulaire de création de groupe
    @PostMapping("/creer") 
    public String creerGroupe(@ModelAttribute("groupe") Groupe groupe, HttpSession session) {
        // Récupère l'utilisateur connecté depuis la session
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateurConnecte");
        if(utilisateur == null){
            return "redirect:/groupes/login";
        }
        // Appelle le service pour enregistrer le groupe
        groupeService.creerGroupe(groupe.getNom(), groupe.getDescription(), utilisateur);

        return "redirect:/groupes/liste"; // Redirige vers la liste des groupes
    }
    // Affiche les groupes de l'utilisateur connecté
    @GetMapping("/liste")
    public String afficherGroupes(Model model, HttpSession session) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateurConnecte");
        if (utilisateur == null) {
            return "redirect:/groupes/login";
        }

        List<Groupe> groupes = groupeService.getGroupesByCreateur(utilisateur);
        model.addAttribute("groupes", groupes); // Ajoute la liste au modèle
        return "listeGroupe"; // Va chercher groupes/liste.html
    }
      @GetMapping("/login")
    public String afficherLogin() {
        return "login"; // va chercher login.html
    }

    @GetMapping("/disponibles")
    public String afficherGroupesDispo(Model model) {
        List<Groupe> groupes = groupeService.getTousLesGroupes();
        model.addAttribute("groupes", groupes);
        return "groupesDisponibles"; // nom de la vue
    }

    @PostMapping("/rejoindre")
    public String rejoindreGroupe(@RequestParam Long idGroupe, HttpSession session) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateurConnecte");
        if (utilisateur == null) {
            return "redirect:/groupes/login";
    }

    groupeService.demanderAdhesion(idGroupe, utilisateur);
    return "redirect:/groupes/liste";
    }
    
    @PostMapping("/annuler")
    public String annulerDemande(Long idGroupe, HttpSession session) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateurConnecte");
        if (utilisateur == null) return "redirect:/groupes/login";

        groupeService.annulerDemande(idGroupe, utilisateur);
        return "redirect:/groupes/disponibles";
    }
    // Voir les demandes pour un groupe (admin)
    @GetMapping("/admin/demandes")
    public String voirDemandes(@RequestParam Long idGroupe, Model model) {
        List<MembreGroupe> demandes = groupeService.getDemandesParGroupe(idGroupe);
        model.addAttribute("demandes", demandes);
        model.addAttribute("idGroupe", idGroupe);
        return "demandesGroupe";
    }

    // Accepter ou refuser
    @PostMapping("/admin/modifierStatut")
    public String modifierStatutMembre(
            @RequestParam Long idMembre,
            @RequestParam String action,
            @RequestParam Long idGroupe) {

        StatutMembre nouveauStatut = action.equals("accepter") ? StatutMembre.ACCEPTE : StatutMembre.REFUSE;
        groupeService.changerStatutMembre(idMembre, nouveauStatut);

        return "redirect:/groupes/admin/demandes?idGroupe=" + idGroupe;
    }
    @GetMapping("/rejoindre")
    public String afficherGroupesDisponibles(Model model, HttpSession session) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateurConnecte");
        if (utilisateur == null) return "redirect:/groupes/login";

        List<Groupe> disponibles = groupeService.getGroupesDisponiblesPour(utilisateur);
        model.addAttribute("groupes", disponibles);
        return "groupesDisponibles"; // Vue à créer
    }


    

}

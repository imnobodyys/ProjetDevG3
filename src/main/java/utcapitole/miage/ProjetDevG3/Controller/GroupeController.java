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

/** Classe GroupeController
 * Gère les groupes de l'application   
 */
@Controller
@RequestMapping("/groupes")
public class GroupeController {

    /** Attributs
     * groupeService : service pour gérer les groupes  
     */
    @Autowired
    private GroupeService groupeService;

    /** Constructeur
     * @param groupeService : service pour gérer les groupes
     */
    public GroupeController(GroupeService groupeService) {
        this.groupeService = groupeService;   
    }
    
    /** Méthode pour afficher le formulaire de création de groupe
     * @param model : modèle pour la vue
     * @return la vue du formulaire de création de groupe
     */
    @GetMapping("/creer")
    public String afficherFormulaire(Model model) {
        model.addAttribute("groupe", new Groupe());// Ajoute un objet vide au modèle
        return "formulaireGroupe"; // Va chercher formulaireGroupe.html
    }

    /** Méthode pour créer un groupe
     * @param groupe : groupe à créer  
     * @param session : session de l'utilisateur
     * @return la vue de la liste des groupes
     */
    @PostMapping("/creer") 
    public String creerGroupe(@ModelAttribute("groupe") Groupe groupe, HttpSession session) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateurConnecte");
        if(utilisateur == null){
            return "redirect:/groupes/login";
        }
        groupeService.creerGroupe(groupe.getNom(), groupe.getDescription(), utilisateur);

        return "redirect:/groupes/liste"; // Redirige vers la liste des groupes
    }
    /**
     * Méthode pour afficher la liste des groupes
     * @param model : modèle pour la vue
     * @param session : session de l'utilisateur
     * @return la vue de la liste des groupes   
     */
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

    /**
     * Méthode pour afficher la page de connexion
     * @return la vue de la page de connexion
     */
      @GetMapping("/login")
    public String afficherLogin() {
        return "login"; // va chercher login.html
    }

    /**
     * Méthode pour afficher la page d'inscription
     * @return la vue de la page d'inscription
     */
    @GetMapping("/disponibles")
    public String afficherGroupesDispo(Model model) {
        List<Groupe> groupes = groupeService.getTousLesGroupes();
        model.addAttribute("groupes", groupes);
        return "groupesDisponibles"; // nom de la vue
    }

    /**
     * Méthode pour rejoindre un groupe
     * @param idGroupe : id du groupe à rejoindre
     * @param session : session de l'utilisateur
     * @return la vue de la liste des groupes
     */
    @PostMapping("/rejoindre")
    public String rejoindreGroupe(@RequestParam Long idGroupe, HttpSession session) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateurConnecte");
        if (utilisateur == null) {
            return "redirect:/groupes/login";
    }

    
     
    groupeService.demanderAdhesion(idGroupe, utilisateur);
    return "redirect:/groupes/liste";
    }
    
    /**
     * Méthode pour annuler une demande d'adhésion à un groupe
     * @param idGroupe : id du groupe
     * @param session : session de l'utilisateur
     * @return la vue de la liste des groupes
     */
    @PostMapping("/annuler")
    public String annulerDemande(Long idGroupe, HttpSession session) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateurConnecte");
        if (utilisateur == null) return "redirect:/groupes/login";

        groupeService.annulerDemande(idGroupe, utilisateur);
        return "redirect:/groupes/disponibles";
    }
    
    /**
     * Méthode pour afficher les demandes d'adhésion à un groupe
     * @param idGroupe : id du groupe
     * @param model : modèle pour la vue
     * @return la vue des demandes d'adhésion
     */
    @GetMapping("/admin/demandes")
    public String voirDemandes(@RequestParam Long idGroupe, Model model) {
        List<MembreGroupe> demandes = groupeService.getDemandesParGroupe(idGroupe);
        model.addAttribute("demandes", demandes);
        model.addAttribute("idGroupe", idGroupe);
        return "demandesGroupe";
    }

    /**
     * Méthode pour accepter ou refuser une demande d'adhésion à un groupe
     * @param idMembre : id du membre
     * @param action : action à effectuer (accepter ou refuser)
     * @param idGroupe : id du groupe
     * @return la vue des demandes d'adhésion
     */
    @PostMapping("/admin/modifierStatut")
    public String modifierStatutMembre(
            @RequestParam Long idMembre,
            @RequestParam String action,
            @RequestParam Long idGroupe) {

        StatutMembre nouveauStatut = action.equals("accepter") ? StatutMembre.ACCEPTE : StatutMembre.REFUSE;
        groupeService.changerStatutMembre(idMembre, nouveauStatut);

        return "redirect:/groupes/admin/demandes?idGroupe=" + idGroupe;
    }

    /**
     * Méthode pour afficher les groupes disponibles pour un utilisateur
     * @param model : modèle pour la vue
     * @param session : session de l'utilisateur
     * @return la vue des groupes disponibles
     */
    @GetMapping("/rejoindre")
    public String afficherGroupesDisponibles(Model model, HttpSession session) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateurConnecte");
        if (utilisateur == null) return "redirect:/groupes/login";

        List<Groupe> disponibles = groupeService.getGroupesDisponiblesPour(utilisateur);
        model.addAttribute("groupes", disponibles);
        return "groupesDisponibles"; // Vue à créer
    }


    

}

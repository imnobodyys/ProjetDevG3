package utcapitole.miage.projetdevg3.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import utcapitole.miage.projetdevg3.model.Groupe;
import utcapitole.miage.projetdevg3.model.MembreGroupe;
import utcapitole.miage.projetdevg3.model.StatutMembre;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.service.GroupeService;
import utcapitole.miage.projetdevg3.service.MembreGroupeService;
import utcapitole.miage.projetdevg3.service.UtilisateurService;

/**
 * Classe GroupeController
 * Gère les groupes de l'application
 */
@Controller
@RequestMapping("/groupes")
public class GroupeController {

    /**
     * Attributs
     * groupeService : service pour gérer les groupes
     */
    @Autowired
    private GroupeService groupeService;

    /** membreGroupeService : service pour gérer les membres de groupe */
    @Autowired
    private MembreGroupeService membreGroupeService;

    @Autowired
    private UtilisateurService utilisateurService;

    /**
     * Constructeur
     * 
     * @param groupeService : service pour gérer les groupes
     */
    public GroupeController(GroupeService groupeService) {
        this.groupeService = groupeService;
    }

    /**
     * Méthode pour afficher le formulaire de création de groupe
     * 
     * @param model : modèle pour la vue
     * @return la vue du formulaire de création de groupe
     */
    @GetMapping("/creer")
    public String afficherFormulaire(Model model) {
        model.addAttribute("groupe", new Groupe());// Ajoute un objet vide au modèle
        return "formulaireGroupe"; // Va chercher formulaireGroupe.html
    }

    /**
     * Méthode pour créer un groupe
     * 
     * @param groupe : groupe à créer
     * @param result : résultat de la validation
     */
    @PostMapping("/creer")
    public String creerGroupe(@ModelAttribute("groupe") @Validated Groupe groupe,
            BindingResult result,
            HttpSession session) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateurConnecte");
        if (utilisateur == null) {
            return "redirect:/groupes/login";
        }
        if (result.hasErrors()) {
            return "formulaireGroupe";
        }
        groupeService.creerGroupe(groupe.getNom(), groupe.getDescription(), utilisateur);
        return "redirect:/groupes/liste";
    }

    /**
     * Méthode pour afficher la liste des groupes
     * 
     * @param model   : modèle pour la vue
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
     * 
     * @return la vue de la page de connexion
     */
    @GetMapping("/login")
    public String afficherLogin() {
        return "login"; // va chercher login.html
    }

    /**
     * Méthode pour afficher la page d'inscription
     * 
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
     * 
     * @param idGroupe : id du groupe à rejoindre
     * @param session  : session de l'utilisateur
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
     * 
     * @param idGroupe : id du groupe
     * @param session  : session de l'utilisateur
     * @return la vue de la liste des groupes
     */
    @PostMapping("/annuler")
    public String annulerDemande(Long idGroupe, HttpSession session) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateurConnecte");
        if (utilisateur == null)
            return "redirect:/groupes/login";

        groupeService.annulerDemande(idGroupe, utilisateur);
        return "redirect:/groupes/disponibles";
    }

    /**
     * Méthode pour afficher les demandes d'adhésion à un groupe
     * 
     * @param idGroupe : id du groupe
     * @param model    : modèle pour la vue
     * @return la vue des demandes d'adhésion
     */
    @GetMapping("/admin/demandes")
    public String voirDemandes(@RequestParam Long idGroupe, Model model, HttpSession session) {

        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateurConnecte");
        Groupe groupe = groupeService.getGroupeById(idGroupe); // ★ AJOUT
        if (!groupe.getCreateur().getId().equals(utilisateur.getId())) {
            return "redirect:/groupes/liste";
        }

        List<MembreGroupe> demandes = groupeService.getDemandesParGroupe(idGroupe);
        model.addAttribute("demandes", demandes);
        model.addAttribute("idGroupe", idGroupe);
        return "demandesGroupe";
    }

    /**
     * Méthode pour afficher les groupes disponibles pour un utilisateur
     * 
     * @param model   : modèle pour la vue
     * @param session : session de l'utilisateur
     * @return la vue des groupes disponibles
     */
    @GetMapping("/rejoindre")
    public String afficherGroupesDisponibles(Model model, HttpSession session) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateurConnecte");
        if (utilisateur == null)
            return "redirect:/groupes/login";

        List<Groupe> disponibles = groupeService.getGroupesDisponiblesPour(utilisateur);

        Map<Long, StatutMembre> statuts = new HashMap<>();
        for (Groupe g : disponibles) {
            StatutMembre statut = groupeService.getStatutPourUtilisateur(g, utilisateur);
            statuts.put(g.getId(), statut);
        }

        model.addAttribute("groupes", disponibles);
        model.addAttribute("statuts", statuts);

        return "groupesDisponibles"; // Vue à créer
    }

    /**
     * Méthode pour accepter une demande d'adhésion
     * 
     * @param idMembre : id du membre
     */
    @PostMapping("/admin/accepter")
    public String accepterMembre(@RequestParam Long idMembre, @RequestParam Long idGroupe) {
        membreGroupeService.accepterMembre(idMembre);
        return "redirect:/groupes/admin/demandes?idGroupe=" + idGroupe;
    }

    /**
     * Méthode pour refuser une demande d'adhésion
     * 
     * @param idMembre : id du membre
     */
    @PostMapping("/admin/refuser")
    public String refuserMembre(@RequestParam Long idMembre, @RequestParam Long idGroupe) {
        membreGroupeService.refuserMembre(idMembre);
        return "redirect:/groupes/admin/demandes?idGroupe=" + idGroupe;
    }

    /**
     * Méthode pour exclure un membre d'un groupe
     * 
     * @param idMembre : id du membre
     */
    @PostMapping("/admin/exclure")
    public String exclureMembre(@RequestParam Long idMembre, @RequestParam Long idGroupe) {
        membreGroupeService.exclureMembre(idMembre);
        return "redirect:/groupes/admin/membres?idGroupe=" + idGroupe;
    }

    /**
     * Méthode pour modifier le statut d'un membre
     * 
     * @param idMembre : id du membre
     */
    @PostMapping("/admin/modifierStatut")
    public String modifierStatut(
            @RequestParam Long idMembre,
            @RequestParam Long idGroupe,
            @RequestParam String action) {

        if ("accepter".equals(action)) {
            membreGroupeService.accepterMembre(idMembre);
        } else if ("refuser".equals(action)) {
            membreGroupeService.refuserMembre(idMembre);
        } else if ("exclure".equals(action)) {
            membreGroupeService.exclureMembre(idMembre);
        }

        return "redirect:/groupes/admin/demandes?idGroupe=" + idGroupe;
    }

    /**
     * Méthode pour afficher les membres d'un groupe
     * 
     * @param idGroupe : id du groupe
     */
    @GetMapping("/admin/membres")
    public String voirMembres(@RequestParam Long idGroupe, Model model, HttpSession session) {

        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateurConnecte"); // ✅
        Groupe groupe = groupeService.getGroupeById(idGroupe);
        if (!groupe.getCreateur().getId().equals(utilisateur.getId())) {
            return "redirect:/groupes/liste";
        }

        List<MembreGroupe> membres = groupeService.getMembresDuGroupe(idGroupe);
        model.addAttribute("membres", membres);
        model.addAttribute("idGroupe", idGroupe);
        return "membresGroupe"; // nom du template Thymeleaf à créer
    }

    @PostMapping("/{id}/supprimer")
    public String supprimerGroupe(@PathVariable Long id, Principal principal) {
        Utilisateur utilisateur = utilisateurService.getUtilisateurByEmail(principal.getName());

        try {
            groupeService.supprimerGroupeSiCreateur(id, utilisateur);
        } catch (SecurityException e) {
            return "redirect:/groupes/liste?erreur=acces";
        }

        return "redirect:/groupes/liste";
    }
}

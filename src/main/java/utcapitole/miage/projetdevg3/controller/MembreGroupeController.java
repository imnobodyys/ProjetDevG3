package utcapitole.miage.projetdevg3.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import utcapitole.miage.projetdevg3.model.Groupe;
import utcapitole.miage.projetdevg3.model.MembreGroupe;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.service.GroupeService;
import utcapitole.miage.projetdevg3.service.MembreGroupeService;
import utcapitole.miage.projetdevg3.service.UtilisateurService;


@Controller
public class MembreGroupeController {

    @Autowired
    private final GroupeService groupeService;
    @Autowired
    private final MembreGroupeService membreGroupeService;
    @Autowired
    private final UtilisateurService utilisateurService;

    @Autowired
    public MembreGroupeController(GroupeService groupeService,
                                  MembreGroupeService membreGroupeService,
                                  UtilisateurService utilisateurService) {
        this.groupeService = groupeService;
        this.membreGroupeService = membreGroupeService;
        this.utilisateurService = utilisateurService;
    }
     // Ajout du token CSRF dans chaque modèle
    @ModelAttribute
    public void addCsrfToken(Model model, CsrfToken token) {
        model.addAttribute("_csrf", token);
    }
    // Afficher les demandes d'adhésion à un groupe (admin only)
    @GetMapping("/membres/demandes")
    public String voirDemandes(@RequestParam Long idGroupe, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/api/utilisateurs/login";
        }
        Utilisateur utilisateur = utilisateurService.findByEmail(principal.getName());
        Groupe groupe = groupeService.getGroupeById(idGroupe);

        // Vérifie que l'utilisateur est bien le créateur du groupe
        if (!groupe.getCreateur().getId().equals(utilisateur.getId())) {
            return "redirect:/groupes/liste?erreur=acces";
        }

        List<MembreGroupe> demandes = groupeService.getDemandesParGroupe(idGroupe);
        model.addAttribute("demandes", demandes);
        model.addAttribute("idGroupe", idGroupe);
        return "demandesGroupe";
    }
     // Afficher les membres d'un groupe (admin only)
    @GetMapping("/membres/liste")
    public String voirMembres(@RequestParam Long idGroupe, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/api/utilisateurs/login";
        }
        Utilisateur utilisateur = utilisateurService.findByEmail(principal.getName());
        Groupe groupe = groupeService.getGroupeById(idGroupe);

        // Vérification créateur
        if (!groupe.getCreateur().getId().equals(utilisateur.getId())) {
            return "redirect:/groupes/liste?erreur=acces";
        }

        List<MembreGroupe> membres = groupeService.getMembresDuGroupe(idGroupe);
        model.addAttribute("membres", membres);
        model.addAttribute("idGroupe", idGroupe);
        return "membresGroupe";
    }
     // Accepter une demande d'adhésion (admin only)
    @PostMapping("/membres/accepter")
    public String accepterMembre(@RequestParam Long idMembre, @RequestParam Long idGroupe, Principal principal) {
        if (principal == null) {
            return "redirect:/api/utilisateurs/login";
        }
        Utilisateur utilisateur = utilisateurService.findByEmail(principal.getName());
        Groupe groupe = groupeService.getGroupeById(idGroupe);

        if (!groupe.getCreateur().getId().equals(utilisateur.getId())) {
            return "redirect:/groupes/liste?erreur=acces";
        }

        membreGroupeService.accepterMembre(idMembre);
        return "redirect:/membres/demandes?idGroupe=" + idGroupe;
    }

    // Refuser une demande d'adhésion (admin only)
    @PostMapping("/membres/refuser")
    public String refuserMembre(@RequestParam Long idMembre, @RequestParam Long idGroupe, Principal principal) {
        if (principal == null) {
            return "redirect:/api/utilisateurs/login";
        }
        Utilisateur utilisateur = utilisateurService.findByEmail(principal.getName());
        Groupe groupe = groupeService.getGroupeById(idGroupe);

        if (!groupe.getCreateur().getId().equals(utilisateur.getId())) {
            return "redirect:/groupes/liste?erreur=acces";
        }

        membreGroupeService.refuserMembre(idMembre);
        return "redirect:/membres/demandes?idGroupe=" + idGroupe;
    }

    // Exclure un membre du groupe (admin only)
    @PostMapping("/membres/exclure")
    public String exclureMembre(@RequestParam Long idMembre, @RequestParam Long idGroupe, Principal principal) {
        if (principal == null) {
            return "redirect:/api/utilisateurs/login";
        }
        Utilisateur utilisateur = utilisateurService.findByEmail(principal.getName());
        Groupe groupe = groupeService.getGroupeById(idGroupe);

        if (!groupe.getCreateur().getId().equals(utilisateur.getId())) {
            return "redirect:/groupes/liste?erreur=acces";
        }

        membreGroupeService.exclureMembre(idMembre);
        return "redirect:/membres/liste?idGroupe=" + idGroupe;
    }

    // Méthode générique pour modifier le statut d'un membre (accepter, refuser, exclure)
    @PostMapping("/membres/modifierStatut")
    public String modifierStatut(@RequestParam Long idMembre,
                                @RequestParam Long idGroupe,
                                @RequestParam String action,
                                Principal principal) {
        if (principal == null) {
            return "redirect:/api/utilisateurs/login";
        }
        Utilisateur utilisateur = utilisateurService.findByEmail(principal.getName());
        Groupe groupe = groupeService.getGroupeById(idGroupe);

        if (!groupe.getCreateur().getId().equals(utilisateur.getId())) {
            return "redirect:/groupes/liste?erreur=acces";
        }

        switch (action) {
            case "accepter":
                membreGroupeService.accepterMembre(idMembre);
                return "redirect:/membres/demandes?idGroupe=" + idGroupe;
            case "refuser":
                membreGroupeService.refuserMembre(idMembre);
                return "redirect:/membres/demandes?idGroupe=" + idGroupe;
            case "exclure":
                membreGroupeService.exclureMembre(idMembre);
                return "redirect:/membres/liste?idGroupe=" + idGroupe;
            default:
                return "redirect:/groupes/liste?erreur=actionInvalide";
        }
    }




}

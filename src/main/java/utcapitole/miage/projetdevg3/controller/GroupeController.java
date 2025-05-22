package utcapitole.miage.projetdevg3.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

     private final GroupeService groupeService;
    private final MembreGroupeService membreGroupeService;
    private final UtilisateurService utilisateurService;

    @Autowired
    public GroupeController(GroupeService groupeService, MembreGroupeService membreGroupeService,
            UtilisateurService utilisateurService) {
        this.groupeService = groupeService;
        this.membreGroupeService = membreGroupeService;
        this.utilisateurService = utilisateurService;
    }

    public GroupeService getGroupeService() {
        return groupeService;
    }

    public MembreGroupeService getMembreGroupeService() {
        return membreGroupeService;
    }

    public UtilisateurService getUtilisateurService() {
        return utilisateurService;
    }

    @ModelAttribute
    public void addCsrfToken(Model model, CsrfToken token) {
        model.addAttribute("_csrf", token);
    }

    @GetMapping("/creer")
    public String afficherFormulaire(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/groupes/login";
        }
        model.addAttribute("groupe", new Groupe());
        return "formulaireGroupe";
    }

    @PostMapping("/creer")
    public String creerGroupe(@ModelAttribute("groupe") @Validated Groupe groupe, BindingResult result, Principal principal,
            RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/groupes/login";
        }
        if (result.hasErrors()) {
            return "formulaireGroupe";
        }
        Utilisateur utilisateur = utilisateurService.findByEmail(principal.getName());
        try {
            // Utiliser la méthode dédiée dans le service qui crée + ajoute créateur comme membre
            groupeService.creerGroupe(groupe.getNom(), groupe.getDescription(), utilisateur);
            redirectAttributes.addFlashAttribute("message", "Groupe créé avec succès.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/groupes/creer";
        }
        return "redirect:/groupes/liste";
    }

    @GetMapping("/liste")
    public String afficherGroupes(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/groupes/login";
        }

        Utilisateur utilisateur = utilisateurService.findByEmail(principal.getName());

        List<Groupe> groupesCrees = groupeService.getGroupesCreesPar(utilisateur);

        // Utiliser la méthode getGroupesDisponiblesPour pour éviter redondance de filtrage
        List<Groupe> groupesAutres = groupeService.getGroupesDisponiblesPour(utilisateur);

        Map<Long, StatutMembre> statuts = new HashMap<>();
        for (Groupe g : groupesAutres) {
            StatutMembre statut = groupeService.getStatutPourUtilisateur(g, utilisateur);
            statuts.put(g.getId(), statut);
        }

        model.addAttribute("groupesCrees", groupesCrees);
        model.addAttribute("groupesAutres", groupesAutres);
        model.addAttribute("statuts", statuts);
        model.addAttribute("username", utilisateur.getEmail());

        return "groupes";
    }
    @GetMapping("/disponibles")
     public String afficherGroupesDisponibles(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/groupes/login";
        }
        Utilisateur utilisateur = utilisateurService.findByEmail(principal.getName());

        List<Groupe> groupesDisponibles = groupeService.getGroupesDisponiblesPourUtilisateur(utilisateur);

        Map<Long, StatutMembre> statuts = new HashMap<>();
        for (Groupe g : groupesDisponibles) {
            StatutMembre statut = groupeService.getStatutPourUtilisateur(g, utilisateur);
            statuts.put(g.getId(), statut);
        }

        model.addAttribute("groupesDisponibles", groupesDisponibles);
        model.addAttribute("statuts", statuts);
        return "groupesDisponibles";
    }

    @PostMapping("/rejoindre")
    public String rejoindreGroupe(@RequestParam Long idGroupe, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/groupes/login";
        }
        Utilisateur utilisateur = utilisateurService.findByEmail(principal.getName());

        boolean estRejointDirectement = groupeService.demanderAdhesion(idGroupe, utilisateur);
        if (estRejointDirectement) {
            redirectAttributes.addFlashAttribute("message", "Vous avez rejoint le groupe avec succès.");
        } else {
            redirectAttributes.addFlashAttribute("message", "Votre demande d’adhésion a été envoyée.");
        }

        return "redirect:/groupes/disponibles";
    }

    

    @GetMapping("/login")
    public String afficherLogin() {
        return "login";
    }

    @PostMapping("/annuler")
    public String annulerDemande(@RequestParam Long idGroupe, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/groupes/login";
        }
        Utilisateur utilisateur = utilisateurService.findByEmail(principal.getName());
        groupeService.annulerDemande(idGroupe, utilisateur);
        redirectAttributes.addFlashAttribute("message", "Votre demande a été annulée.");
        return "redirect:/groupes/disponibles";
    }

    @PostMapping("/supprimer")
    public String supprimerGroupe(@RequestParam("idGroupe") Long idGroupe, Principal principal,
            RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/groupes/login";
        }
        Utilisateur utilisateur = utilisateurService.findByEmail(principal.getName());
        Groupe groupe = groupeService.getGroupeById(idGroupe);

        if (groupe != null && groupe.getCreateur().equals(utilisateur)) {
            groupeService.supprimerGroupe(groupe);
            redirectAttributes.addFlashAttribute("message", "Groupe supprimé avec succès.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Vous n'êtes pas autorisé à supprimer ce groupe.");
        }

        return "redirect:/groupes/liste";
    }

    @GetMapping("/demandes-envoyees")
    public String afficherDemandesEnvoyees(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/groupes/login";
        }

        Utilisateur utilisateur = utilisateurService.findByEmail(principal.getName());

        // Récupérer les groupes avec une demande en attente
        List<Groupe> groupesEnAttente = groupeService.getGroupesAvecDemandeEnAttente(utilisateur);

        model.addAttribute("groupes", groupesEnAttente);
        return "demandesEnvoyees";
    }

    @GetMapping("/{id}/gerer-membres")
    public String gererMembres(@PathVariable Long id, Model model, Principal principal,
            RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/groupes/login";
        }
        Utilisateur utilisateur = utilisateurService.findByEmail(principal.getName());
        Groupe groupe = groupeService.getGroupeById(id);

        if (groupe == null || !groupe.getCreateur().equals(utilisateur)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vous n'êtes pas autorisé à gérer ce groupe.");
            return "redirect:/groupes/liste";
        }

        List<MembreGroupe> membresAcceptes = membreGroupeService.getMembresParGroupeEtStatut(groupe,
                StatutMembre.ACCEPTE);
        List<MembreGroupe> demandesEnAttente = membreGroupeService.getMembresParGroupeEtStatut(groupe,
                StatutMembre.EN_ATTENTE);

        model.addAttribute("groupe", groupe);
        model.addAttribute("membresAcceptes", membresAcceptes);
        model.addAttribute("demandesEnAttente", demandesEnAttente);

        return "gererMembres";
    }

    
   

   
}

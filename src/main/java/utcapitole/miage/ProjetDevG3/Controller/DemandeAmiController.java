package utcapitole.miage.projetDevG3.Controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import utcapitole.miage.projetDevG3.Service.DemandeAmiService;
import utcapitole.miage.projetDevG3.model.DemandeAmi;
import utcapitole.miage.projetDevG3.model.Utilisateur;
import utcapitole.miage.projetDevG3.Repository.UtilisateurRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * classe DemandeAmiController
 * Gère les demandes d'amis entre utilisateurs
 * 
 * @author [Votre nom]
 */
@Controller
@RequestMapping("/demandes")
@RequiredArgsConstructor
public class DemandeAmiController {
    
    private final DemandeAmiService demandeAmiService = null;
    private final UtilisateurRepository utilisateurRepository = null;

    /**
     * 
     * @param model
     * @return page pour confirmer
     */
    @GetMapping("/form/{destinataireId}")
    public String demandeamiid(@PathVariable Long destinaireId, Model model) {
        model.addAttribute("destinaireId", destinaireId);
        return "form_demande";
    }

    /**
     * 
     * @param model
     * @return page pour confirmer
     */
    @GetMapping("/form")
    public String manudemandeAmiid(Model model) {
        model.addAttribute("demandeId", true);
        return "form_demande";
    }

    /**
     * envoyer demande
     * 
     * @param destinataireId
     * @param principal
     * @param redirectAttributes
     * @return page user
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/envoyer")
    public String envoyerDemande(
            @RequestParam Long destinataireId,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        String email = principal.getName();
        Utilisateur currentUser = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        demandeAmiService.envoyerdemandeami(currentUser.getId(), destinataireId);

        redirectAttributes.addFlashAttribute("success", "sucess! ");
        return "redirect:/users";
    }

    /**
     * controller pour avoir lisr de demande par autre utilisateur
     * 
     * @param principal
     * @param model
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/recues")
    public String getDemandesRecues(Principal principal, Model model) {
        Utilisateur currentUser = utilisateurRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        List<DemandeAmi> demandes = demandeAmiService.getDemandesRecuesEnAttente(currentUser);

        List<Map<String, Object>> demandeList = demandes.stream()
                .map(d -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", d.getId());
                    map.put("expediteur", d.getExpediteurAmi().getNom());
                    map.put("date", d.getDtEnvoi());
                    return map;
                })
                .collect(Collectors.toList());
        model.addAttribute("demandes", demandeList);

        return "demandes-recues"; // templates/demandes-recues.html
    }

    /**
     * pour accepter une demande
     * 
     * @param id
     * @param principal
     * @return page recues
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/accepter/{id}")
    public String accepterDemande(
            @PathVariable Long id,
            Principal principal) {

        Utilisateur currentUser = utilisateurRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        demandeAmiService.accepterDemande(id, currentUser);
        return "redirect:/demandes/recues";
    }

    /**
     * pour refuser une demande
     * 
     * @param id
     * @param principal
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/refuser/{id}")
    public String refuserDemande(
            @PathVariable Long id,
            Principal principal) {

        Utilisateur currentUser = utilisateurRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        demandeAmiService.refuserDemande(id, currentUser);
        return "redirect:/demandes/recues";
    }

    /**
     * pour voir list des amis
     * 
     * @param model
     * @param principal
     * @return page list des amis
     */
    @GetMapping("/amis")
    @PreAuthorize("isAuthenticated()")
    public String voirMesAmis(Model model, Principal principal) {
        Utilisateur currentUser = utilisateurRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        List<Utilisateur> amis = demandeAmiService.getAmis(currentUser);

        model.addAttribute("amis", amis);
        return "amis";
    }

    /**
     * suprimer un ami
     * 
     * @param id
     * @param principal
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/supprier/{id}")
    public String supprierami(@PathVariable Long id, Principal principal) {
        Utilisateur currenUtilisateur = utilisateurRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        demandeAmiService.supprimeramis(currenUtilisateur, id);
        return "redirect:/demandes/amis";
    }

}

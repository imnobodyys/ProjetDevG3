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
    private final DemandeAmiService demandeAmiService;
    private final UtilisateurRepository utilisateurRepository;

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
     * 
     * @param destinataireId
     * @param principal
     * @param redirectAttributes
     * @return page user
     */
    @PostMapping("/envoyer")
    public String envoyerDemande(
            @RequestParam Long destinataireId,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        String email = principal.getName();
        Utilisateur currentUser = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        demandeAmiService.envoyerdemandeami(currentUser.getId(), destinataireId);

        redirectAttributes.addFlashAttribute("success", "好友请求已发送！");
        return "redirect:/users";
    }

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

    @PostMapping("/accepter/{id}")
    public String accepterDemande(
            @PathVariable Long id,
            Principal principal) {

        Utilisateur currentUser = utilisateurRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        demandeAmiService.accepterDemande(id, currentUser);
        return "redirect:/demandes/recues";
    }

    @PostMapping("/refuser/{id}")
    public String refuserDemande(
            @PathVariable Long id,
            Principal principal) {

        Utilisateur currentUser = utilisateurRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        demandeAmiService.refuserDemande(id, currentUser);
        return "redirect:/demandes/recues";
    }
}

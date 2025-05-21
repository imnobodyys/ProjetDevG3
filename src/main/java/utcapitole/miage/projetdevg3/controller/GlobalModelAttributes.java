
package utcapitole.miage.projetdevg3.controller;

import java.security.Principal;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.repository.UtilisateurRepository;

@Component
@ControllerAdvice
public class GlobalModelAttributes {

    private final UtilisateurRepository utilisateurRepository;

    public GlobalModelAttributes(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @ModelAttribute("utilisateurconnet")
    public Utilisateur addUtilisateurToModel(Principal principal) {
        if (principal != null) {
            return utilisateurRepository.findByEmail(principal.getName())
                    .orElse(null);
        }
        return null;
    }

    @ModelAttribute("isAuthenticated")
    public boolean addAuthFlag(Principal principal) {
        return principal != null;
    }
}

package utcapitole.miage.projetDevG3.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import utcapitole.miage.projetDevG3.Repository.UtilisateurRepository;
import utcapitole.miage.projetDevG3.model.Utilisateur;

/**
 * Service pour la gestion des utilisateurs.
 * Administre les opérations CRUD et la logique métier liée aux comptes
 * utilisateurs.
 */
@Service
@RequiredArgsConstructor
public class UtilisateurService {

    /**
     * Référentiel pour les utilisateurs.
     * Utilisé pour interagir avec la base de données.
     */
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * US01
     * création de profile personnel avec cryptage du mot de passe
     * 
     * @param utilisateur Objet utilisateur à créer
     * @return Utilisateur créé
     * @throws IllegalArgumentException Si l'email existe déjà
     */
    public Utilisateur creerUtilisateur(Utilisateur utilisateur) {
        if (utilisateurRepository.findByEmail(utilisateur.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Cet email est déjà utilisé !");
        }

        utilisateur.setDtInscription(LocalDateTime.now());

        utilisateur.setMdp(passwordEncoder.encode(utilisateur.getMdp()));

        return utilisateurRepository.save(utilisateur);

    }
    
    public List<Utilisateur> rechercher(String keyword) {
        return utilisateurRepository.searchByKeyword(keyword);
    }
}

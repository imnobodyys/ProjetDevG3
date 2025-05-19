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
public class UtilisateurService {

    /**
     * Référentiel pour les utilisateurs.
     * Utilisé pour interagir avec la base de données.
     */
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurService(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }
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


    /**
     * US03 - Récupérer un utilisateur par email
     * @param email Email de l'utilisateur
     * @return Utilisateur correspondant
     * @throws IllegalArgumentException Si l'utilisateur n'existe pas
     */
    public Utilisateur getUtilisateurByEmail(String email) {
        return utilisateurRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
    }

    
    /**
     * US03 - Modification de profil utilisateur
     * Met à jour les informations d'un utilisateur existant
     * 
     * @param id Identifiant de l'utilisateur à modifier
     * @param utilisateurDetails Nouveaux détails de l'utilisateur
     * @return Utilisateur mis à jour
     * @throws IllegalArgumentException Si l'utilisateur n'existe pas ou email déjà utilisé
     */
    public Utilisateur modifierUtilisateur(Long id, Utilisateur utilisateurDetails) {
        Utilisateur existingUser = utilisateurRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        
        // Vérification email unique
        if(!existingUser.getEmail().equals(utilisateurDetails.getEmail())) {
            if(utilisateurRepository.findByEmail(utilisateurDetails.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Cet email est déjà utilisé");
            }
        }
        
        // Mise à jour des champs modifiables
        existingUser.setNom(utilisateurDetails.getNom());
        existingUser.setPrenom(utilisateurDetails.getPrenom());
        existingUser.setEmail(utilisateurDetails.getEmail());
        
        // Mise à jour mot de passe si fourni
        if(utilisateurDetails.getMdp() != null && !utilisateurDetails.getMdp().isEmpty()) {
            existingUser.setMdp(passwordEncoder.encode(utilisateurDetails.getMdp()));
        }
        
        return utilisateurRepository.save(existingUser);
    }
    

    /**
     * US04 - Suppression de profil
     * Supprime un utilisateur de la base de données
     * 
     * @param id Identifiant de l'utilisateur à supprimer
     * @throws IllegalArgumentException Si l'utilisateur n'existe pas
     */
    public void supprimerUtilisateur(Long id) {
        if (!utilisateurRepository.existsById(id)) {
            throw new IllegalArgumentException("Utilisateur non trouvé");
        }
        utilisateurRepository.deleteById(id);
    }

    public List<Utilisateur> rechercher(String keyword) {
        return utilisateurRepository.searchByKeyword(keyword);
    }
}

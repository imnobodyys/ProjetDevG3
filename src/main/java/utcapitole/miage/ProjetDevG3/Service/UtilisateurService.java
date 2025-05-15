package utcapitole.miage.projetDevG3.Service;

<<<<<<< HEAD
import java.time.LocalDateTime;
=======
import java.util.List;
>>>>>>> main

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import utcapitole.miage.projetDevG3.Repository.UtilisateurRepository;
import utcapitole.miage.projetDevG3.model.Utilisateur;

/**
 * Service pour la gestion des utilisateurs.
 * Administre les opérations CRUD et la logique métier liée aux comptes
 * utilisateurs.
 */
@Service
public class UtilisateurService {

<<<<<<< HEAD
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * US01
     * création de profile personnel avec cryptage du mot de passe
     * @param utilisateur Objet utilisateur à créer
     * @return Utilisateur créé
     * @throws IllegalArgumentException Si l'email existe déjà
     */
    public Utilisateur creerUtilisateur(Utilisateur utilisateur){
        if(utilisateurRepository.findByEmail(utilisateur.getEmail()).isPresent()){
            throw new IllegalArgumentException("Cet email est déjà utilisé !");
        }

        utilisateur.setDtInscription(LocalDateTime.now());

        utilisateur.setMdp(passwordEncoder.encode(utilisateur.getMdp()));

        return utilisateurRepository.save(utilisateur);
        
    }

=======
    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    public List<Utilisateur> rechercher(String keyword) {
        return utilisateurRepository.searchByKeyword(keyword);
    }
>>>>>>> main
}

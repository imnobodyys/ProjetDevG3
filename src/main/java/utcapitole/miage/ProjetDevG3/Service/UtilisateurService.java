package utcapitole.miage.projetDevG3.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private UtilisateurRepository utilisateurRepository;

    public List<Utilisateur> rechercher(String keyword) {
        return utilisateurRepository.searchByKeyword(keyword);
    }
}

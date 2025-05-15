package utcapitole.miage.projetDevG3.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utcapitole.miage.projetDevG3.Repository.GroupeRepository;

/**
 * Service pour la gestion des groupes.
 * Administre les opérations liées aux collections d'utilisateurs.
 */
@Service
public class GroupeService {
    @Autowired
    private GroupeRepository groupeRepository;

}
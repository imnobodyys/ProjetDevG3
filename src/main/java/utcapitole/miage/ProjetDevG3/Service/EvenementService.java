package utcapitole.miage.projetDevG3.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utcapitole.miage.projetDevG3.Repository.EvenementRepository;

/**
 * Service pour la gestion des événements.
 * Contrôle la création, modification et suppression des activités programmées.
 */
@Service
public class EvenementService {
    @Autowired
    private EvenementRepository evenementRepository;

}
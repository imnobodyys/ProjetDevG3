package utcapitole.miage.projetDevG3.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utcapitole.miage.projetDevG3.Repository.EvenementRepository;
import utcapitole.miage.projetDevG3.model.Evenement;

/**
 * Service pour la gestion des événements.
 * Contrôle la création, modification et suppression des activités programmées.
 */
@Service
public class EvenementService {

    /**
     * Référentiel pour les événements.
     * Utilisé pour effectuer des opérations CRUD sur les événements.
     */
    @Autowired
    private EvenementRepository evenementRepository;

    /**
     * US43 - Création d'un nouvel événement avec validation
     * @param evenement Événement à créer
     * @return Événement créé
     * @throws IllegalArgumentException Si les champs obligatoires sont manquants
     */
    public Evenement creerEvenement(Evenement evenement) {
        if(evenement.getTitre() == null || evenement.getTitre().isBlank()) {
            throw new IllegalArgumentException("Le titre est obligatoire");
        }
        if(evenement.getDescription() == null || evenement.getDescription().isBlank()) {
            throw new IllegalArgumentException("La description est obligatoire");
        }
        if(evenement.getDatePublication() == null) {
            throw new IllegalArgumentException("Date invalide");
        }
        return evenementRepository.save(evenement);
    }
}
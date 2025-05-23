package utcapitole.miage.projetdevg3.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import utcapitole.miage.projetdevg3.model.Evenement;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.model.VisibiliteEvenement;
import utcapitole.miage.projetdevg3.repository.EvenementRepository;

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
     * Récupère un événement par son ID
     * 
     * @param id Identifiant de l'événement
     * @return Événement correspondant
     * @throws IllegalArgumentException Si l'événement n'existe pas
     */
    public Evenement getEvenementById(Long id) {
        return evenementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Événement non trouvé"));
    }
    /**
     * Récupère les événements publics
     * @return Liste des événements avec visibilité PUBLIC
     */
    public List<Evenement> getEvenementsPublics() {
        return evenementRepository.findByVisibilite(VisibiliteEvenement.PUBLIC);
    }

    /**
     * Récupère les événements créés par un utilisateur
     * @param auteur Utilisateur créateur
     * @return Liste des événements
     */
    public List<Evenement> getEvenementsParAuteur(Utilisateur auteur) {
        return evenementRepository.findByAuteur(auteur);
    }

    /**
     * Récupère les événements où un utilisateur est inscrit
     * @param participant Utilisateur participant
     * @return Liste des événements
     */
    public List<Evenement> getEvenementsParParticipant(Utilisateur participant) {
        return evenementRepository.findByParticipantsContaining(participant);
    }

    /**
     * 
     * US43 - Création d'un nouvel événement avec validation
     * 
     * @param evenement Événement à créer
     * @return Événement créé
     * @throws IllegalArgumentException Si les champs obligatoires sont manquants
     */
    public Evenement creerEvenement(Evenement evenement) {
        if (evenement.getTitre() == null || evenement.getTitre().isBlank()) {
            throw new IllegalArgumentException("Le titre est obligatoire");
        }
        if (evenement.getDescription() == null || evenement.getDescription().isBlank()) {
            throw new IllegalArgumentException("La description est obligatoire");
        }
        if (evenement.getDatePublication() == null) {
            throw new IllegalArgumentException("Date invalide");
        }
        return evenementRepository.save(evenement);
    }

    /**
     * US44 - Modifier un événement 
     * Modification d'un événement existant avec validation des droits
     * 
     * @param id              Identifiant de l'événement à modifier
     * @param nouvelEvenement Nouvelles données de l'événement
     * @param currentUser     Utilisateur actuellement authentifié
     * @return Événement mis à jour
     * @throws IllegalArgumentException Si l'événement n'existe pas ou l'utilisateur
     *                                  n'est pas l'auteur
     */
    public Evenement modifierEvenement(Long id, Evenement nouvelEvenement, Utilisateur currentUser) {
        Evenement existingEvent = evenementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Événement non trouvé"));

        // Vérification des droits
        if (!existingEvent.getAuteur().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Seul l'auteur peut modifier l'événement");
        }
        // Mise à jour des champs modifiables
        existingEvent.setTitre(nouvelEvenement.getTitre());
        existingEvent.setDescription(nouvelEvenement.getDescription());
        existingEvent.setContenu(nouvelEvenement.getContenu());
        existingEvent.setVisibilite(nouvelEvenement.getVisibilite());

        // Validation des champs obligatoires
        if (existingEvent.getTitre() == null || existingEvent.getTitre().isBlank()) {
            throw new IllegalArgumentException("Le titre est obligatoire");
        }
        if (existingEvent.getDescription() == null || existingEvent.getDescription().isBlank()) {
            throw new IllegalArgumentException("La description est obligatoire");
        }

        return evenementRepository.save(existingEvent);
    }

    /**
     * US45 - Suppression d'un événement
     * 
     * @param id          ID de l'événement
     * @param currentUser Utilisateur actuel
     * @throws IllegalArgumentException Si l'événement n'existe pas ou droits
     *                                  insuffisants
     */
    public void supprimerEvenement(Long id, Utilisateur currentUser) {
        Evenement event = evenementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Événement non trouvé"));

        if (!event.getAuteur().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Seul l'auteur peut supprimer l'événement");
        }

        evenementRepository.deleteById(id);
    }

    /**
     * US47 - Participer à un événement
     *  Ajoute un participant à un événement
     * @param id ID de l'événement
     * @param participant Utilisateur à ajouter
     * @return Événement mis à jour
     * @throws IllegalArgumentException Si l'événement n'existe pas ou participant
     *                                  déjà inscrit
     */
    public Evenement participerEvenement(Long id, Utilisateur participant) {
        Evenement evenement = evenementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Événement non trouvé"));

        if (evenement.getParticipants().contains(participant)) {
            throw new IllegalArgumentException("Vous êtes déjà inscrit à cet événement");
        }

        evenement.addParticipant(participant);
        return evenementRepository.save(evenement);
    }


    /**
     * US48 - Visualisation des participants à un événement
     * Récupère la liste des participants d'un événement
     * @param id ID de l'événement
     * @return Liste des participants
     * @throws IllegalArgumentException Si l'événement n'existe pas
     */
    public List<Utilisateur> getParticipantsEvenement(Long id){
        Evenement evenement = evenementRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Événement non trouvé"));

            return evenement.getParticipants();

    }

}
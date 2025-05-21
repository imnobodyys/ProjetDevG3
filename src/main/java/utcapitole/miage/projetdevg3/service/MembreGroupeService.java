package utcapitole.miage.projetdevg3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import utcapitole.miage.projetdevg3.model.MembreGroupe;
import utcapitole.miage.projetdevg3.model.StatutMembre;
import utcapitole.miage.projetdevg3.repository.MembreGroupeRepository;

/**
 * Service pour la gestion des membres de groupe.
 * Traite l'affectation et les droits des participants dans un groupe.
 */
@Service
public class MembreGroupeService {
    @Autowired
    private MembreGroupeRepository membreGroupeRepository;

    /**
     * Accepter un membre (statut = ACCEPTE)
     * 
     * @param idMembre l'identifiant du membre à accepter
     * @throws IllegalArgumentException si le membre n'est pas trouvé
     */
    public void accepterMembre(Long idMembre) {
        MembreGroupe membre = membreGroupeRepository.findById(idMembre)
                .orElseThrow(() -> new IllegalArgumentException("Membre non trouvé"));
        membre.setStatut(StatutMembre.ACCEPTE);
        membreGroupeRepository.save(membre);
    }

    /**
     * Refuser un membre (statut = REFUSE)
     * 
     * @param idMembre l'identifiant du membre à refuser
     */
    public void refuserMembre(Long idMembre) {
        MembreGroupe membre = membreGroupeRepository.findById(idMembre)
                .orElseThrow(() -> new IllegalArgumentException("Membre non trouvé"));
        membre.setStatut(StatutMembre.REFUSE);
        membreGroupeRepository.save(membre);
    }

    /**
     * Exclure un membre (suppression de l'enregistrement)
     * 
     * @param idMembre l'identifiant du membre à exclure
     */
    public void exclureMembre(Long idMembre) {
        MembreGroupe membre = membreGroupeRepository.findById(idMembre)
                .orElseThrow(() -> new IllegalArgumentException("Membre non trouvé"));
        membreGroupeRepository.delete(membre);
    }

}
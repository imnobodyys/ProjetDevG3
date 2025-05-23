package utcapitole.miage.projetdevg3.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import utcapitole.miage.projetdevg3.model.Groupe;
import utcapitole.miage.projetdevg3.model.MembreGroupe;
import utcapitole.miage.projetdevg3.model.StatutMembre;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.repository.MembreGroupeRepository;

/**
 * Service pour la gestion des membres de groupe.
 * Traite l'affectation et les droits des participants dans un groupe.
 */
@Service
public class MembreGroupeService {

   
    private final MembreGroupeRepository membreGroupeRepository;
     private final GroupeService groupeService;


    @Autowired
    public MembreGroupeService(MembreGroupeRepository membreGroupeRepository, GroupeService groupeService) {
        this.membreGroupeRepository = membreGroupeRepository;
         this.groupeService = groupeService;
    }

     public List<MembreGroupe> getMembresParGroupeEtStatut(Groupe groupe, StatutMembre statut) {
        return membreGroupeRepository.findByGroupeAndStatut(groupe, statut);
    }
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

    public List<Groupe> getGroupesDisponiblesPourUtilisateur(Utilisateur utilisateur) {
        List<Groupe> tousLesGroupes = getTousLesGroupes();

        return tousLesGroupes.stream()
            .filter(groupe -> 
                !groupe.getCreateur().equals(utilisateur) &&                       // pas créateur
                getStatutPourUtilisateur(groupe, utilisateur) == null              // pas encore membre ou en attente
            )
            .collect(Collectors.toList());
    }

    // Exemple d’existant : méthode qui retourne le statut d’un utilisateur dans un groupe
   public StatutMembre getStatutPourUtilisateur(Groupe groupe, Utilisateur utilisateur) {
    return membreGroupeRepository.findByGroupeAndMembreUtilisateur(groupe, utilisateur)
            .map(MembreGroupe::getStatut)
            .orElse(null);
  }

    // Méthode pour récupérer tous les groupes
    public List<Groupe> getTousLesGroupes() {
        return groupeService.getTousLesGroupes();
        
    }

}
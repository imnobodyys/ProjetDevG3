package utcapitole.miage.projetDevG3.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import utcapitole.miage.projetDevG3.Repository.GroupeRepository;
import utcapitole.miage.projetDevG3.Repository.MembreGroupeRepository;
import utcapitole.miage.projetDevG3.model.Groupe;
import utcapitole.miage.projetDevG3.model.Groupe.TypeGroupe;
import utcapitole.miage.projetDevG3.model.MembreGroupe;
import utcapitole.miage.projetDevG3.model.StatutMembre;
import utcapitole.miage.projetDevG3.model.Utilisateur;

/**
 * Service pour la gestion des groupes.
 * Administre les opérations liées aux collections d'utilisateurs.
 */
@Service
public class GroupeService {
    @Autowired
    private GroupeRepository groupeRepository;

    @Autowired
    private MembreGroupeRepository MembreGroupeRepository;
    
    @Autowired
    public GroupeService(GroupeRepository groupeRepository,
            utcapitole.miage.projetDevG3.Repository.MembreGroupeRepository membreGroupeRepository) {
        this.groupeRepository = groupeRepository;
        MembreGroupeRepository = membreGroupeRepository;
    }

    public Groupe getGroupeById(Long id) {
    return groupeRepository.findById(id).orElseThrow();
    }
    /**
     * US17 - Crée un nouveau groupe et ajoute automatiquement le créateur comme membre accepté.
     */
    public Groupe creerGroupe(String nom, String description,Utilisateur createur) {
        if (groupeRepository.existsByNomIgnoreCase(nom)) {
        throw new IllegalArgumentException("Ce nom de groupe est déjà utilisé.");
    }
          // Création du groupe
        Groupe groupe = new Groupe();
        groupe.setNom(nom);
        groupe.setDescription(description);
        groupe.setCreateur(createur);

        
        groupe = groupeRepository.save(groupe); 

        // Le créateur devient membre accepté
        MembreGroupe membre = new MembreGroupe();
        membre.setGroupe(groupe);
        membre.setMembre(createur);
        membre.setStatut(StatutMembre.ACCEPTE);
        MembreGroupeRepository.save(membre);

    return groupe;
           
    }

    public List<MembreGroupe> getMembresDuGroupe(Long idGroupe) {
    Groupe groupe = groupeRepository.findById(idGroupe).orElseThrow();
    return MembreGroupeRepository.findByGroupe(groupe);
    }
    /**
     * Récupère tous les groupes créés par un utilisateur.
     */
    public List<Groupe> getGroupesByCreateur(Utilisateur createur) {
        return groupeRepository.findByCreateur(createur);
    }

    public void demanderAdhesion(Long idGroupe, Utilisateur utilisateur) {
    Groupe groupe = groupeRepository.findById(idGroupe).orElseThrow();

    boolean dejaMembreOuEnAttente = MembreGroupeRepository.findByMembre(utilisateur).stream()
            .anyMatch(mg -> mg.getGroupe().getId().equals(idGroupe));
        if (dejaMembreOuEnAttente) {
            return; // Ne fait rien si déjà membre ou en attente
        }

        StatutMembre statut = (groupe.getType() == TypeGroupe.PUBLIC)
        ? StatutMembre.ACCEPTE
        : StatutMembre.EN_ATTENTE;

    MembreGroupe membre = new MembreGroupe();
    membre.setGroupe(groupe);
    membre.setMembre(utilisateur);
    membre.setStatut(statut);
    
    MembreGroupeRepository.save(membre);
    }

    public List<Groupe> getGroupesDisponiblesPour(Utilisateur utilisateur) {
        List<MembreGroupe> dejaRejoints = MembreGroupeRepository.findByMembre(utilisateur);
        List<Long> idsGroupes = dejaRejoints.stream()
            .map(m -> m.getGroupe().getId())
            .toList();

        return groupeRepository.findAll().stream()
            .filter(g -> !idsGroupes.contains(g.getId()))
            .toList();
    }

    public void annulerDemande(Long idGroupe, Utilisateur utilisateur) {
    Groupe groupe = groupeRepository.findById(idGroupe).orElseThrow();
    
     List<MembreGroupe> membresEnAttente = MembreGroupeRepository.findByMembre(utilisateur).stream()
            .filter(m -> m.getGroupe().getId().equals(idGroupe) && m.getStatut() == StatutMembre.EN_ATTENTE)
            .toList();

        // Supprime toutes les demandes en attente trouvées
        MembreGroupeRepository.deleteAll(membresEnAttente);
    }


    // Pour récupérer les demandes en attente
    public List<MembreGroupe> getDemandesParGroupe(Long idGroupe) {
        Groupe groupe = groupeRepository.findById(idGroupe).orElseThrow();
        return MembreGroupeRepository.findByGroupeAndStatut(groupe, StatutMembre.EN_ATTENTE);
    }

    // Pour changer le statut (accepter ou refuser)
    public void changerStatutMembre(Long idMembre, StatutMembre statut) {
        MembreGroupe membre = MembreGroupeRepository.findById(idMembre).orElseThrow();
        membre.setStatut(statut);
        MembreGroupeRepository.save(membre);
    }
     public List<Groupe> getTousLesGroupes() {
        return groupeRepository.findAll();
    }
     public MembreGroupeRepository getMembreGroupeRepository() {
         return MembreGroupeRepository;
     }
     public GroupeRepository getGroupeRepository() {
         return groupeRepository;
     }
     public void setGroupeRepository(GroupeRepository groupeRepository) {
         this.groupeRepository = groupeRepository;
     }
     public void setMembreGroupeRepository(MembreGroupeRepository membreGroupeRepository) {
         MembreGroupeRepository = membreGroupeRepository;
     }

    public StatutMembre getStatutPourUtilisateur(Groupe groupe, Utilisateur utilisateur) {
    return MembreGroupeRepository.findByMembre(utilisateur).stream()
            .filter(m -> m.getGroupe().getId().equals(groupe.getId()))
            .map(MembreGroupe::getStatut)
            .findFirst()
            .orElse(null);
    }

}
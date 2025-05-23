package utcapitole.miage.projetdevg3.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import utcapitole.miage.projetdevg3.model.Groupe;
import utcapitole.miage.projetdevg3.model.Groupe.TypeGroupe;
import utcapitole.miage.projetdevg3.repository.GroupeRepository;
import utcapitole.miage.projetdevg3.repository.MembreGroupeRepository;
import utcapitole.miage.projetdevg3.model.MembreGroupe;
import utcapitole.miage.projetdevg3.model.StatutMembre;
import utcapitole.miage.projetdevg3.model.Utilisateur;

/**
 * Service pour la gestion des groupes.
 * Administre les opérations liées aux collections d'utilisateurs.
 */
@Service
public class GroupeService {

@Autowired
    private GroupeRepository groupeRepository;

    @Autowired
    private MembreGroupeRepository membreGroupeRepository;

    @Autowired
    public GroupeService(GroupeRepository groupeRepository,
            MembreGroupeRepository membreGroupeRepository) {
        this.groupeRepository = groupeRepository;
        this.membreGroupeRepository = membreGroupeRepository;  // correction ici (ajout du this)
    }

    public Groupe getGroupeById(Long id) {
        return groupeRepository.findById(id).orElseThrow();
    }

    /**
     * US17 - Crée un nouveau groupe et ajoute automatiquement le créateur comme
     * membre accepté.
     */
    public Groupe creerGroupe(String nom, String description, Utilisateur createur) {
        if (groupeRepository.existsByNomIgnoreCase(nom)) {
            throw new IllegalArgumentException("Ce nom de groupe est déjà utilisé.");
        }
        Groupe groupe = new Groupe();
        groupe.setNom(nom);
        groupe.setDescription(description);
        groupe.setCreateur(createur);

        groupe = groupeRepository.save(groupe);

        MembreGroupe membre = new MembreGroupe();
        membre.setGroupe(groupe);
        membre.setMembreUtilisateur(createur);
        membre.setStatut(StatutMembre.ACCEPTE);
        membreGroupeRepository.save(membre);

        return groupe;
    }

    public List<MembreGroupe> getMembresDuGroupe(Long idGroupe) {
        Groupe groupe = groupeRepository.findById(idGroupe).orElseThrow();
        return membreGroupeRepository.findByGroupe(groupe);
    }

    public List<Groupe> getGroupesByCreateur(Utilisateur createur) {
        return groupeRepository.findByCreateur(createur);
    }

    /**
     * Demander adhésion à un groupe (statut selon type de groupe).
     */
    public boolean demanderAdhesion(Long idGroupe, Utilisateur utilisateur) {
        Groupe groupe = groupeRepository.findById(idGroupe).orElseThrow(() -> new RuntimeException("Groupe non trouvé"));

        boolean dejaMembreOuEnAttente = membreGroupeRepository.findByMembreUtilisateur(utilisateur).stream()
                .anyMatch(mg -> mg.getGroupe().getId().equals(idGroupe));

        if (dejaMembreOuEnAttente) {
            return false;
        }

        StatutMembre statut = (groupe.getType() == TypeGroupe.PUBLIC) ? StatutMembre.ACCEPTE : StatutMembre.EN_ATTENTE;

        MembreGroupe membre = new MembreGroupe(utilisateur, groupe);
        membre.setStatut(statut);

        membreGroupeRepository.save(membre);

        return statut == StatutMembre.ACCEPTE;
    }

    /**
     * Récupère les groupes disponibles (créés par d'autres utilisateurs) que
     * l'utilisateur ne suit pas encore.
     */
    public List<Groupe> getGroupesDisponiblesPour(Utilisateur utilisateur) {
         List<Long> idsGroupesDejaRejoints = membreGroupeRepository
            .findByMembreUtilisateur(utilisateur)
            .stream()
            .map(m -> m.getGroupe().getId())
            .toList();

    
    return groupeRepository.findAll().stream()
            .filter(g -> !idsGroupesDejaRejoints.contains(g.getId()))
            .filter(g -> g.getCreateur() != null && !g.getCreateur().getId().equals(utilisateur.getId()))
            .toList();
    }

    public void annulerDemande(Long idGroupe, Utilisateur utilisateur) {
        Groupe groupe = groupeRepository.findById(idGroupe).orElseThrow();

        List<MembreGroupe> membresEnAttente = membreGroupeRepository.findByMembreUtilisateur(utilisateur).stream()
                .filter(m -> m.getGroupe().getId().equals(idGroupe) && m.getStatut() == StatutMembre.EN_ATTENTE)
                .toList();

        membreGroupeRepository.deleteAll(membresEnAttente);
    }

    public List<MembreGroupe> getDemandesParGroupe(Long idGroupe) {
        Groupe groupe = groupeRepository.findById(idGroupe).orElseThrow();
        return membreGroupeRepository.findByGroupeAndStatut(groupe, StatutMembre.EN_ATTENTE);
    }

    public void changerStatutMembre(Long idMembre, StatutMembre statut) {
        MembreGroupe membre = membreGroupeRepository.findById(idMembre).orElseThrow();
        membre.setStatut(statut);
        membreGroupeRepository.save(membre);
    }

    public void supprimerGroupeSiCreateur(Long idGroupe, Utilisateur utilisateur) {
        Groupe groupe = groupeRepository.findById(idGroupe)
                .orElseThrow(() -> new IllegalArgumentException("Groupe non trouvé"));

        if (!groupe.getCreateur().getId().equals(utilisateur.getId())) {
            throw new SecurityException("Seul le créateur peut supprimer ce groupe");
        }

        groupeRepository.deleteById(idGroupe);
    }

    public List<Groupe> getTousLesGroupes() {
        return groupeRepository.findAll();
    }

    public MembreGroupeRepository getMembreGroupeRepository() {
        return membreGroupeRepository;
    }

    public GroupeRepository getGroupeRepository() {
        return groupeRepository;
    }

    public void setGroupeRepository(GroupeRepository groupeRepository) {
        this.groupeRepository = groupeRepository;
    }

    public void setMembreGroupeRepository(MembreGroupeRepository membreGroupeRepository) {
        this.membreGroupeRepository = membreGroupeRepository;  // correction ici aussi
    }

    public StatutMembre getStatutPourUtilisateur(Groupe groupe, Utilisateur utilisateur) {
        return membreGroupeRepository.findByMembreUtilisateur(utilisateur).stream()
                .filter(m -> m.getGroupe().getId().equals(groupe.getId()))
                .map(MembreGroupe::getStatut)
                .findFirst()
                .orElse(null);
    }

    public void sauvegarderGroupe(Groupe groupe) {
        groupeRepository.save(groupe);
    }

    public List<Groupe> getGroupesAvecDemandeEnAttente(Utilisateur utilisateur) {
        List<MembreGroupe> demandes = membreGroupeRepository.findByMembreUtilisateurAndStatut(utilisateur, StatutMembre.EN_ATTENTE);
        return demandes.stream()
                .map(MembreGroupe::getGroupe)
                .collect(Collectors.toList());
    }

    public void supprimerGroupe(Groupe groupe) {
        groupeRepository.delete(groupe);
    }

    public List<Groupe> getGroupesCreesPar(Utilisateur utilisateur) {
        return groupeRepository.findByCreateur(utilisateur);
    }

    public List<MembreGroupe> getMembresDuGroupe(Groupe groupe) {
        return membreGroupeRepository.findByGroupe(groupe);
    }

    public List<MembreGroupe> getDemandesEnAttentePourGroupe(Groupe groupe) {
        return membreGroupeRepository.findByGroupeAndStatut(groupe, StatutMembre.EN_ATTENTE);
    }

    public void exclureMembre(Long idMembreGroupe) {
        membreGroupeRepository.deleteById(idMembreGroupe);
    }
    public List<Groupe> getGroupesDisponiblesPourUtilisateur(Utilisateur utilisateur) {
    List<Groupe> tous = groupeRepository.findAll();

    return tous.stream()
        .filter(g -> {
            StatutMembre statut = getStatutPourUtilisateur(g, utilisateur);
            return statut == null || statut == StatutMembre.REFUSE;
        })
        .collect(Collectors.toList());
}
    

    
}

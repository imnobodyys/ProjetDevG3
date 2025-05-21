package utcapitole.miage.projetdevg3.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import utcapitole.miage.projetdevg3.model.DemandeAmi;
import utcapitole.miage.projetdevg3.model.StatutDemande;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.repository.DemandeAmiRepository;
import utcapitole.miage.projetdevg3.repository.UtilisateurRepository;

/**
 * Service pour la gestion des demandes d'amitié.
 * Gère l'envoi, l'acceptation et le refus des invitations entre utilisateurs.
 */
@Service
@Transactional
public class DemandeAmiService {
    private final UtilisateurRepository utilisateurRepository;
    private final DemandeAmiRepository demandeAmiRepository;

    public DemandeAmiService(DemandeAmiRepository demandeAmiRepository, UtilisateurRepository utilisateurRepository) {
        this.demandeAmiRepository = demandeAmiRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * Envoie une demande d'ami
     * 
     * @throws IllegalArgumentException si l'utilisateur n'existe pas ou tentative
     *                                  d'auto-ajout
     * @throws IllegalStateException    si une demande existe déjà
     */
    public void envoyerDemandeAmi(Long expediteurId, Long destinataireId) {
        if (expediteurId.equals(destinataireId)) {
            throw new IllegalArgumentException("Vous ne pouvez pas vous ajouter vous-même comme ami");
        }

        if (demandeAmiRepository.existsDemandeBetween(expediteurId, destinataireId)) {
            throw new IllegalStateException("Une demande existe déjà entre ces utilisateurs");
        }

        Utilisateur expediteur = utilisateurRepository.findById(expediteurId)
                .orElseThrow(() -> new IllegalArgumentException("Expéditeur non trouvé"));

        Utilisateur destinataire = utilisateurRepository.findById(destinataireId)
                .orElseThrow(() -> new IllegalArgumentException("Destinataire non trouvé"));

        DemandeAmi demande = new DemandeAmi();
        demande.setExpediteurAmi(expediteur);
        demande.setDestinataireAmi(destinataire);
        demande.setStatut(StatutDemande.EN_ATTENTE);
        demande.setDtEnvoi(LocalDateTime.now());

        demandeAmiRepository.save(demande);
    }

    /**
     * Récupère les demandes reçues en attente pour un utilisateur
     */
    public List<DemandeAmi> getDemandesRecuesEnAttente(Utilisateur destinataire) {
        return demandeAmiRepository.findByDestinataireAmiAndStatut(
                destinataire,
                StatutDemande.EN_ATTENTE);
    }

    /**
     * Accepte une demande d'ami
     * 
     * @throws IllegalStateException si la demande n'est pas en attente
     */
    @Transactional
    public void accepterDemande(Long demandeId, Utilisateur currentUser) {
        DemandeAmi demande = demandeAmiRepository.findById(demandeId)
                .orElseThrow(() -> new IllegalArgumentException("Demande introuvable"));

        if (!demande.getDestinataireAmi().equals(currentUser)) {
            throw new IllegalStateException("Vous n'avez pas le droit d'accepter cette demande");
        }

        if (demande.getStatut() != StatutDemande.EN_ATTENTE) {
            throw new IllegalStateException("Cette demande a déjà été traitée");
        }

        demande.setStatut(StatutDemande.ACCEPTE);
        demandeAmiRepository.save(demande);
    }

    /**
     * Refuse une demande d'ami
     */
    @Transactional
    public void refuserDemande(Long demandeId, Utilisateur currentUser) {
        DemandeAmi demande = demandeAmiRepository.findById(demandeId)
                .orElseThrow(() -> new IllegalArgumentException("Demande introuvable"));

        if (!demande.getDestinataireAmi().equals(currentUser)) {
            throw new IllegalStateException("Vous n'avez pas le droit de refuser cette demande");
        }

        if (demande.getStatut() != StatutDemande.EN_ATTENTE) {
            throw new IllegalStateException("Cette demande a déjà été traitée");
        }

        demande.setStatut(StatutDemande.REFUSE);
        demandeAmiRepository.save(demande);
    }

    /**
     * Récupère la liste des amis d'un utilisateur
     */
    public List<Utilisateur> getAmis(Utilisateur utilisateur) {
        List<DemandeAmi> demandes = demandeAmiRepository
                .findByStatutAndDestinataireAmiOrStatutAndExpediteurAmi(
                        StatutDemande.ACCEPTE, utilisateur,
                        StatutDemande.ACCEPTE, utilisateur);

        return demandes.stream()
                .map(d -> d.getExpediteurAmi().equals(utilisateur)
                        ? d.getDestinataireAmi()
                        : d.getExpediteurAmi())
                .collect(Collectors.toList());
    }

    /**
     * Supprime une amitié entre deux utilisateurs
     */
    @Transactional
    public void supprimerAmi(Utilisateur utilisateur, Long amiId) {
        Utilisateur ami = utilisateurRepository.findById(amiId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur ami non trouvé"));

        demandeAmiRepository.deleteAmitie(utilisateur, ami);
    }
}

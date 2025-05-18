package utcapitole.miage.projetDevG3.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import utcapitole.miage.projetDevG3.Repository.DemandeAmiRepository;
import utcapitole.miage.projetDevG3.Repository.UtilisateurRepository;
import utcapitole.miage.projetDevG3.model.DemandeAmi;
import utcapitole.miage.projetDevG3.model.StatutDemande;
import utcapitole.miage.projetDevG3.model.Utilisateur;

/**
 * Service pour la gestion des demandes d'amitié.
 * Gère l'envoi, l'acceptation et le refus des invitations entre utilisateurs.
 */
@Service
@RequiredArgsConstructor
public class DemandeAmiService {
    /**
     * Référentiel pour les utilisateurs.
     * Utilisé pour interagir avec la base de données des utilisateurs.
     */
    private UtilisateurRepository utilisateurRepository;
    private DemandeAmiRepository demandeAmiRepository;

    /**
     * method pour envoyer demande ami
     * 
     * @param expediteur
     * @param destinaire
     *                   Envoie une demande d'amitié d'un utilisateur à un autre.
     *                   Vérifie si l'expéditeur et le destinataire sont différents.
     */
    public void envoyerdemandeami(Long expediteur, Long destinaire) {
        if (expediteur.equals(destinaire)) {
            throw new IllegalArgumentException("ne peuvez pas ajourer vous meme");
        }

        if (demandeAmiRepository.existsDemandeBetween(expediteur,
                destinaire)) {
            throw new IllegalArgumentException("Deja demande");
        }
        DemandeAmi demande = new DemandeAmi();
        demande.setExpediteurAmi(utilisateurRepository.getReferenceById(expediteur));
        demande.setDestinataireAmi(utilisateurRepository.getReferenceById(destinaire));
        demandeAmiRepository.save(demande);
    }

    /**
     * method pour visualiser list de demande ami
     * 
     * @param destinataire
     * @return
     */
    public List<DemandeAmi> getDemandesRecues(Utilisateur destinataire) {
        return demandeAmiRepository.findByDestinataireAmiAndStatut(destinataire, StatutDemande.EN_ATTENTE);
    }

    @Transactional
    public void accepterDemande(Long demandeId, Utilisateur currentUser) {
        DemandeAmi demande = demandeAmiRepository.findById(demandeId)
                .orElseThrow(() -> new IllegalArgumentException("Demande introuvable"));

        // 验证当前用户是请求的接收方
        if (!demande.getDestinataireAmi().equals(currentUser)) {
            throw new IllegalStateException("vous n'avez pas drois");
        }

        // 验证状态是EN_ATTENTE
        if (demande.getStatut() != StatutDemande.EN_ATTENTE) {
            throw new IllegalStateException("Demande est fini");
        }

        // 更新状态
        demande.setStatut(StatutDemande.ACCEPTE);
        demandeAmiRepository.save(demande);

    }

    @Transactional
    public void refuserDemande(Long demandeId, Utilisateur currentUser) {
        DemandeAmi demande = demandeAmiRepository.findById(demandeId)
                .orElseThrow(() -> new IllegalArgumentException("Demande introuvable"));

        if (!demande.getDestinataireAmi().equals(currentUser)) {
            throw new IllegalStateException("vous n'avez pas drois");
        }

        if (demande.getStatut() != StatutDemande.EN_ATTENTE) {
            throw new IllegalStateException("Demande est fini");
        }

        demande.setStatut(StatutDemande.REFUSE);
        demandeAmiRepository.save(demande);
    }

    public List<DemandeAmi> getDemandesRecuesEnAttente(Utilisateur destinataire) {
        return demandeAmiRepository.findByDestinataireAndStatut(
                destinataire,
                StatutDemande.EN_ATTENTE);
    }

}

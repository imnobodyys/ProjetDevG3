package utcapitole.miage.projetDevG3.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private UtilisateurRepository utilisateurRepository;
    private DemandeAmiRepository demandeAmiRepository;

    /**
     * method pour envoyer demande ami
     * 
     * @param expediteur
     * @param destinaire
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

}

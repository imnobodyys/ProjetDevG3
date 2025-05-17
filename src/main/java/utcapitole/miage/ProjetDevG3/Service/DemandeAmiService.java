package utcapitole.miage.projetDevG3.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import utcapitole.miage.projetDevG3.Repository.DemandeAmiRepository;
import utcapitole.miage.projetDevG3.Repository.UtilisateurRepository;
import utcapitole.miage.projetDevG3.model.DemandeAmi;
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
     * Envoie une demande d'amitié d'un utilisateur à un autre.
     * Vérifie si l'expéditeur et le destinataire sont différents.
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
}
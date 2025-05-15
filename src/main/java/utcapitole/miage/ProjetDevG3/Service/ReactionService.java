package utcapitole.miage.projetDevG3.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utcapitole.miage.projetDevG3.Repository.ReactionRepository;

/**
 * Service pour la gestion des réactions utilisateur.
 * Gère les interactions comme les likes ou les émoticônes sur les contenus.
 */
@Service
public class ReactionService {
    @Autowired
    private ReactionRepository reactionRepository;

}
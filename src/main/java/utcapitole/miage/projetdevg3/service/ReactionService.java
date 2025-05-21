package utcapitole.miage.projetdevg3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import utcapitole.miage.projetdevg3.repository.ReactionRepository;

/**
 * Service pour la gestion des réactions utilisateur.
 * Gère les interactions comme les likes ou les émoticônes sur les contenus.
 */
@Service
public class ReactionService {

    /**
     * Référentiel pour les réactions.
     * Utilisé pour effectuer des opérations CRUD sur les réactions.
     */
    @Autowired
    private ReactionRepository reactionRepository;

}
package utcapitole.miage.projetdevg3.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import utcapitole.miage.projetdevg3.model.Post;
import utcapitole.miage.projetdevg3.model.Reaction;
import utcapitole.miage.projetdevg3.model.TypeReaction;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.repository.ReactionRepository;

/**
 * Service pour la gestion des réactions utilisateur.
 * Gère les interactions comme les likes ou les émoticônes sur les contenus.
 */
@Service
@RequiredArgsConstructor
public class ReactionService {

    private final ReactionRepository reactionRepository;

    public void ajouterOuModifierReaction(Utilisateur utilisateur, Post post, TypeReaction type) {
        reactionRepository.findByExpedientAndPost(utilisateur, post).ifPresentOrElse(
                reactionExistante -> {
                    reactionExistante.setType(type); // mise à jour
                    reactionRepository.save(reactionExistante);
                },
                () -> {
                    Reaction reaction = new Reaction();
                    reaction.setPost(post);
                    reaction.setExpedient(utilisateur);
                    reaction.setType(type);
                    reaction.setDtEnvoi(LocalDateTime.now());
                    reactionRepository.save(reaction);
                });
    }

    public Map<TypeReaction, Long> compterReactions(Post post) {
        return reactionRepository.findByPost(post).stream()
                .collect(Collectors.groupingBy(Reaction::getType, Collectors.counting()));
    }
}

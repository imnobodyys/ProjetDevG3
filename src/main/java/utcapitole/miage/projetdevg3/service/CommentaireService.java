package utcapitole.miage.projetdevg3.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import utcapitole.miage.projetdevg3.model.Commentaire;
import utcapitole.miage.projetdevg3.model.Post;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.repository.CommentaireRepository;

@Service
@RequiredArgsConstructor
public class CommentaireService {

    private final CommentaireRepository commentaireRepository;

    public void ajouterCommentaire(Utilisateur expediteur, Post post, String contenu) {
        Commentaire commentaire = new Commentaire();
        commentaire.setContenu(contenu);
        commentaire.setExpediteur(expediteur);
        commentaire.setPost(post);
        commentaire.setDateEnvoi(LocalDateTime.now());

        commentaireRepository.save(commentaire);
    }

    public List<Commentaire> getCommentaires(Post post) {
        return commentaireRepository.findByPostOrderByDtEnvoiAsc(post);
    }
}

package utcapitole.miage.projetdevg3.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import utcapitole.miage.projetdevg3.model.Commentaire;
import utcapitole.miage.projetdevg3.model.ConversationGrp;
import utcapitole.miage.projetdevg3.model.ConversationPri;
import utcapitole.miage.projetdevg3.model.Message;
import utcapitole.miage.projetdevg3.model.Post;
import utcapitole.miage.projetdevg3.model.TypeReaction;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.service.CommentaireService;
import utcapitole.miage.projetdevg3.service.ConversationService;
import utcapitole.miage.projetdevg3.service.MessageService;
import utcapitole.miage.projetdevg3.service.PostService;
import utcapitole.miage.projetdevg3.service.ReactionService;
import utcapitole.miage.projetdevg3.service.UtilisateurService;
import utcapitole.miage.projetdevg3.repository.UtilisateurRepository;

@Controller
@RequestMapping("/posts")

public class PostController {
    private final CommentaireService commentaireService;
    private final PostService postService;
    private final UtilisateurService utilisateurService;
    private final ReactionService reactionService;

    public PostController(PostService postService, UtilisateurService utilisateurService,
            CommentaireService commentaireService, ReactionService reactionService) {
        this.postService = postService;
        this.utilisateurService = utilisateurService;
        this.commentaireService = commentaireService;
        this.reactionService = reactionService;
    }

    @GetMapping("/nouveau")
    @PreAuthorize("isAuthenticated()")
    public String afficherFormulaire(Model model) {
        model.addAttribute("post", new Post());
        return "form-post";
    }

    @PostMapping("/publier")
    @PreAuthorize("isAuthenticated()")
    public String publierPost(@ModelAttribute Post post,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        Utilisateur auteur = utilisateurService.findByEmail(principal.getName());

        postService.creerPost(post, auteur);
        redirectAttributes.addFlashAttribute("success", "Post publié !");
        return "redirect:/posts/liste";
    }

    private void ajouterStatsReaction(List<Post> posts, Model model) {
        Map<Long, Map<TypeReaction, Long>> stats = new HashMap<>();
        for (Post post : posts) {
            stats.put(post.getId(), reactionService.compterReactions(post));
        }
        model.addAttribute("reactionsStats", stats);
    }

    @GetMapping("/liste")
    @PreAuthorize("isAuthenticated()")
    public String afficherPosts(Model model, Principal principal) {
        List<Post> posts = postService.getPostsPublics();
        model.addAttribute("posts", posts);
        model.addAttribute("pageType", "tous");

        Map<Long, Map<TypeReaction, Long>> reactionsStats = new HashMap<>();
        posts.forEach(post -> {
            reactionsStats.put(post.getId(), reactionService.compterReactions(post));
        });
        model.addAttribute("reactionsStats", reactionsStats);

        if (principal != null) {
            Utilisateur utilisateur = utilisateurService.findByEmail(principal.getName());
            model.addAttribute("utilisateurConnecte", utilisateur);
        }
        System.out.println("Reactions Stats: " + reactionsStats);
        return "list-post";
    }

    @GetMapping("/mes-posts")
    @PreAuthorize("isAuthenticated()")
    public String afficherMesPosts(Model model, Principal principal) {
        Utilisateur utilisateur = utilisateurService.findByEmail(principal.getName());
        List<Post> posts = postService.getPostsParAuteur(utilisateur);

        model.addAttribute("posts", posts);
        model.addAttribute("utilisateurConnecte", utilisateur);
        model.addAttribute("pageType", "mes");

        Map<Long, Map<TypeReaction, Long>> reactionsStats = new HashMap<>();
        posts.forEach(post -> {
            reactionsStats.put(post.getId(), reactionService.compterReactions(post));
        });
        model.addAttribute("reactionsStats", reactionsStats);
        System.out.println("Reactions Stats: " + reactionsStats);
        return "list-post";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/supprimer/{id}")
    public String supprimerPost(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        Utilisateur auteur = utilisateurService.findByEmail(principal.getName());

        boolean success = postService.supprimerPost(id, auteur);
        if (success) {
            redirectAttributes.addFlashAttribute("success", "Post supprimé avec succès !");
        } else {
            redirectAttributes.addFlashAttribute("error", "Vous n'êtes pas autorisé à supprimer ce post.");
        }

        return "redirect:/posts/liste";
    }

    @GetMapping("/voir/{id}")
    public String voirPost(@PathVariable Long id, Model model, Principal principal) {
        Post post = postService.getPostById(id);
        List<Commentaire> commentaires = commentaireService.getCommentaires(post);

        model.addAttribute("post", post);
        model.addAttribute("commentaires", commentaires);
        model.addAttribute("nouveauCommentaire", new Commentaire());

        return "voir-post";
    }

    @PostMapping("/{postId}/commenter")
    @PreAuthorize("isAuthenticated()")
    public String commenterPost(@PathVariable Long postId,
            @RequestParam String contenu,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        Utilisateur utilisateur = utilisateurService.findByEmail(principal.getName());

        postService.ajouterCommentaire(postId, contenu, utilisateur);

        redirectAttributes.addFlashAttribute("success", "Commentaire ajouté !");
        return "redirect:/posts/liste";
    }

    @PostMapping("/{postId}/reaction")
    @PreAuthorize("isAuthenticated()")
    public String reagirAuPost(@PathVariable Long postId,
            @RequestParam("type") TypeReaction type,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        Utilisateur utilisateur = utilisateurService.findByEmail(principal.getName());
        Post post = postService.getPostById(postId);

        reactionService.ajouterOuModifierReaction(utilisateur, post, type);

        redirectAttributes.addFlashAttribute("success", "Réaction enregistrée !");
        System.out.println("Reaction type: " + type);
        System.out.println("Post ID: " + postId);
        return "redirect:/posts/liste";
    }

}

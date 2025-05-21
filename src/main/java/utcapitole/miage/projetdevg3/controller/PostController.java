package utcapitole.miage.projetdevg3.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import utcapitole.miage.projetdevg3.model.ConversationGrp;
import utcapitole.miage.projetdevg3.model.ConversationPri;
import utcapitole.miage.projetdevg3.model.Message;
import utcapitole.miage.projetdevg3.model.Post;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.service.ConversationService;
import utcapitole.miage.projetdevg3.service.MessageService;
import utcapitole.miage.projetdevg3.service.PostService;
import utcapitole.miage.projetdevg3.service.UtilisateurService;
import utcapitole.miage.projetdevg3.repository.UtilisateurRepository;

@Controller
@RequestMapping("/posts")

public class PostController {

    private final PostService postService;
    private final UtilisateurService utilisateurService;

    public PostController(PostService postService, UtilisateurService utilisateurService) {
        this.postService = postService;
        this.utilisateurService = utilisateurService;
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

    @GetMapping("/liste")

    @PreAuthorize("isAuthenticated()")
    public String afficherPosts(Model model, Principal principal) {
        List<Post> posts = postService.getPostsPublics();
        model.addAttribute("posts", posts);

        if (principal != null) {
            Utilisateur utilisateur = utilisateurService.findByEmail(principal.getName());

            model.addAttribute("utilisateurConnecte", utilisateur);
        }

        return "list-post";
    }

    @GetMapping("/mes-posts")
    @PreAuthorize("isAuthenticated()")
    public String afficherMesPosts(Model model, Principal principal) {
        Utilisateur utilisateur = utilisateurService.findByEmail(principal.getName());
        model.addAttribute("posts", postService.getPostsParAuteur(utilisateur));
        model.addAttribute("utilisateurConnecte", utilisateur);
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

}

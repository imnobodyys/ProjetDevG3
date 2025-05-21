package utcapitole.miage.projetdevg3.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import utcapitole.miage.projetdevg3.service.PostService;
import utcapitole.miage.projetdevg3.service.UtilisateurService;
import utcapitole.miage.projetdevg3.service.GroupeService;
import utcapitole.miage.projetdevg3.model.Groupe;
import utcapitole.miage.projetdevg3.model.Post;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.repository.GroupeRepository;
import utcapitole.miage.projetdevg3.repository.UtilisateurRepository;

@Controller
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private GroupeRepository groupeRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private GroupeService groupeService;

    @Autowired
    private UtilisateurService utilisateurService;

    @GetMapping("/nouveau")
    public String formNouveauPost(@PathVariable Long groupeId, Model model) {
        Groupe groupe = groupeRepository.findById(groupeId)
                .orElseThrow(() -> new IllegalArgumentException("Groupe non trouvé"));

        Post post = new Post();
        post.setGroupe(groupe);

        model.addAttribute("post", post);
        model.addAttribute("groupe", groupe);
        return "posts/nouveauPost"; // Vue Thymeleaf
    }

    @PostMapping
    public String creerPost(@PathVariable Long groupeId, @ModelAttribute Post post,
            @RequestParam Long auteurId, Model model) {
        Groupe groupe = groupeRepository.findById(groupeId)
                .orElseThrow(() -> new IllegalArgumentException("Groupe non trouvé"));

        Utilisateur auteur = utilisateurRepository.findById(auteurId)
                .orElseThrow(() -> new IllegalArgumentException("Auteur non trouvé"));

        post.setGroupe(groupe);
        post.setAuteur(auteur);

        try {
            postService.publierDansGroupe(post);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("post", post);
            model.addAttribute("groupe", groupe);
            return "posts/nouveauPost"; // Retour au formulaire avec message d'erreur
        }

        return "redirect:/groupes/" + groupeId + "/posts";
    }

    @GetMapping
    public String listerPosts(@PathVariable Long groupeId, Model model) {
        Groupe groupe = groupeRepository.findById(groupeId)
                .orElseThrow(() -> new IllegalArgumentException("Groupe non trouvé"));

        model.addAttribute("groupe", groupe);
        model.addAttribute("posts", postService.getPostsParGroupe(groupeId));

        return "posts/groupePosts"; // Vue Thymeleaf pour afficher les posts
    }

    @GetMapping("/groupes/{groupeId}/posts")
    public String afficherPosts(@PathVariable Long groupeId, Model model) {
        Groupe groupe = groupeService.getGroupeById(groupeId);
        List<Post> posts = postService.getPostsParGroupe(groupeId);
        model.addAttribute("groupe", groupe);
        model.addAttribute("posts", posts);
        return "groupe-posts";
    }

    @GetMapping("/groupes/{groupeId}/posts/nouveau")
    public String afficherFormulairePost(@PathVariable Long groupeId, Model model) {
        Groupe groupe = groupeService.getGroupeById(groupeId);
        model.addAttribute("groupe", groupe);
        model.addAttribute("post", new Post());
        return "groupe-post-form";
    }

    @PostMapping("/groupes/{groupeId}/posts")
    public String publierPost(@PathVariable Long groupeId, @ModelAttribute Post post, Principal principal) {
        Utilisateur auteur = utilisateurService.getUtilisateurByEmail(principal.getName());
        Groupe groupe = groupeService.getGroupeById(groupeId);

        post.setAuteur(auteur);
        post.setGroupe(groupe);
        postService.publierDansGroupe(post);

        return "redirect:/groupes/" + groupeId + "/posts";
    }

}

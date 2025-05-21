package utcapitole.miage.projetdevg3.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import utcapitole.miage.projetdevg3.model.Groupe;
import utcapitole.miage.projetdevg3.model.Post;
import utcapitole.miage.projetdevg3.model.Utilisateur;
import utcapitole.miage.projetdevg3.repository.MembreGroupeRepository;
import utcapitole.miage.projetdevg3.repository.PostRepository;
import utcapitole.miage.projetdevg3.service.PostService;

class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private MembreGroupeRepository membreGroupeRepository;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

}

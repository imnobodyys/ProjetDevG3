package utcapitole.miage.projetDevG3.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utcapitole.miage.projetDevG3.Repository.MembreGroupeRepository;

/**
 * Service pour la gestion des membres de groupe.
 * Traite l'affectation et les droits des participants dans un groupe.
 */
@Service
public class MembreGroupeService {
    @Autowired
    private MembreGroupeRepository membreGroupeRepository;

}
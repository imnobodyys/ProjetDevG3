package utcapitole.miage.projetDevG3.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import utcapitole.miage.projetDevG3.model.Groupe;

public interface GroupeRepository extends JpaRepository<Groupe, Long> {
    // Méthode pour trouver un groupe par son ID
    Groupe findById(long id);
    
   
}

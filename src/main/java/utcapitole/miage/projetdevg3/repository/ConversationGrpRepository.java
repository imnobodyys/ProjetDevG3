package utcapitole.miage.projetdevg3.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import utcapitole.miage.projetdevg3.model.ConversationGrp;
import utcapitole.miage.projetdevg3.model.Utilisateur;

@Repository
public interface ConversationGrpRepository extends JpaRepository<ConversationGrp, Long> {
    // Méthode pour trouver une conversation de groupe par son ID
    Optional<ConversationGrp> findByGroupeCon_Id(Long groupeId);

    List<ConversationGrp> findByGroupeCon_Membres_membreUtilisateur(Utilisateur user);

}

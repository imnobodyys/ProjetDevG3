package utcapitole.miage.projetDevG3.Repository;

public interface ConversationGrp extends ConversationRepository {
    // Méthode pour trouver une conversation de groupe par son ID
    ConversationGrp findById(Long id);
    // Méthode pour trouver une conversation de groupe par son nom
    ConversationGrp findByNom(String nom);  
     

}

package com.entreprise.gestioncommandes.repository;

import com.entreprise.gestioncommandes.model.LigneCommande;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LigneCommandeRepository extends JpaRepository<LigneCommande, Long> {

    boolean existsByProduitId(Long produitId);
}

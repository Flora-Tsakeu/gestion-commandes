package com.entreprise.gestioncommandes.repository;

import com.entreprise.gestioncommandes.dto.TopProduitVendu;
import com.entreprise.gestioncommandes.model.LigneCommande;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LigneCommandeRepository extends JpaRepository<LigneCommande, Long> {

    boolean existsByProduitId(Long produitId);

    @Query("select new com.entreprise.gestioncommandes.dto.TopProduitVendu("
            + "l.produit.reference, l.produit.libelle, sum(l.quantite)) "
            + "from LigneCommande l where l.commande.annulee = false "
            + "group by l.produit.reference, l.produit.libelle "
            + "order by sum(l.quantite) desc")
    List<TopProduitVendu> trouverTopProduitsVendus(Pageable pageable);
}

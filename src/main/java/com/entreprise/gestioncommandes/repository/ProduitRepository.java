package com.entreprise.gestioncommandes.repository;

import com.entreprise.gestioncommandes.dto.ResumeStockCategorie;
import com.entreprise.gestioncommandes.model.Produit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProduitRepository extends JpaRepository<Produit, Long> {

    Optional<Produit> findByReference(String reference);

    List<Produit> findByQuantiteStockLessThanEqual(Integer seuil);

    Page<Produit> findByCategorie(String categorie, Pageable pageable);

    @Query("select p from Produit p where p.quantiteStock <= p.seuilAlerte")
    List<Produit> findEnDessousDeLeurSeuilAlerte();

    @Query("select new com.entreprise.gestioncommandes.dto.ResumeStockCategorie("
            + "p.categorie, count(p), sum(p.prixUnitaireHt * p.quantiteStock)) "
            + "from Produit p group by p.categorie")
    List<ResumeStockCategorie> resumerStockParCategorie();

}

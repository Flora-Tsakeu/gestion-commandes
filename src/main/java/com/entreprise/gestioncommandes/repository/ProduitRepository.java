package com.entreprise.gestioncommandes.repository;

import com.entreprise.gestioncommandes.model.Produit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProduitRepository extends JpaRepository<Produit, Long> {

    Optional<Produit> findByReference(String reference);

    List<Produit> findByQuantiteStockLessThanEqual(Integer seuil);

     Page<Produit> findByCategorie(String categorie, Pageable pageable);
}

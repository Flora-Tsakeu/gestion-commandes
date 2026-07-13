package com.entreprise.gestioncommandes.service;

import com.entreprise.gestioncommandes.model.Produit;
import com.entreprise.gestioncommandes.repository.ProduitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProduitService {

    private static final Logger log = LoggerFactory.getLogger(ProduitService.class);

    private final ProduitRepository produitRepository;

    public ProduitService(ProduitRepository produitRepository) {
        this.produitRepository = produitRepository;
    }

    public List<Produit> listerProduits() {
        return produitRepository.findAll();
    }

    public Produit recupererParId(Long id) {
        return produitRepository.findById(id).orElse(null);
               
    }

    public Produit creerProduit(Produit produit) {
        Produit enregistre = produitRepository.save(produit);
        log.info("produit cree, reference={}, stock initial={}", enregistre.getReference(), enregistre.getQuantiteStock());
        return enregistre;
    }

    
}

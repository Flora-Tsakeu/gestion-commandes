package com.entreprise.gestioncommandes.service;

import com.entreprise.gestioncommandes.exception.ProduitIntrouvableException;
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
        return produitRepository.findById(id)
                .orElseThrow(() -> new ProduitIntrouvableException(id));
               
    }

    public Produit recupererParReference(String reference) {
        return produitRepository.findByReference(reference)
                .orElseThrow(() -> new ProduitIntrouvableException(reference));
    }


    public Produit creerProduit(Produit produit) {
        Produit enregistre = produitRepository.save(produit);
        log.info("produit cree, reference={}, stock initial={}", enregistre.getReference(), enregistre.getQuantiteStock());
        return enregistre;
    }

    public Produit mettreAJour(Long id, Produit modifications) {
        Produit existant = recupererParId(id);
        existant.setLibelle(modifications.getLibelle());
        existant.setPrixUnitaireHt(modifications.getPrixUnitaireHt());
        existant.setQuantiteStock(modifications.getQuantiteStock());
        Produit maj = produitRepository.save(existant);
        log.info("produit mis a jour, id={}, nouveau stock={}", id, maj.getQuantiteStock());
        return maj;
    }

    public void supprimer(Long id) {
        Produit existant = recupererParId(id);
        produitRepository.delete(existant);
        log.info("produit supprime, id={}", id);
    }

    
}

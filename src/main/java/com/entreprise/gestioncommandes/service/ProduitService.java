package com.entreprise.gestioncommandes.service;

import com.entreprise.gestioncommandes.dto.ResumeStockCategorie;
import com.entreprise.gestioncommandes.exception.ProduitIntrouvableException;
import com.entreprise.gestioncommandes.exception.ProduitReferenceParCommandeException;
import com.entreprise.gestioncommandes.exception.ReferenceProduitDejaUtiliseeException;
import com.entreprise.gestioncommandes.model.Produit;
import com.entreprise.gestioncommandes.repository.LigneCommandeRepository;
import com.entreprise.gestioncommandes.repository.ProduitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProduitService {

    private static final Logger log = LoggerFactory.getLogger(ProduitService.class);

    private final ProduitRepository produitRepository;
    private final LigneCommandeRepository ligneCommandeRepository;


    public ProduitService(ProduitRepository produitRepository, LigneCommandeRepository ligneCommandeRepository) {
        this.produitRepository = produitRepository;
        this.ligneCommandeRepository = ligneCommandeRepository;
    }

    public Page<Produit> listerProduits(Pageable pageable) {
        return produitRepository.findAll(pageable);
    }

    public Page<Produit> listerParCategorie(String categorie, Pageable pageable) {
        return produitRepository.findByCategorie(categorie, pageable);
    }

    public Page<Produit> rechercherParLibelle(String texte, Pageable pageable) {
        return produitRepository.findByLibelleContainingIgnoreCase(texte, pageable);
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
        produitRepository.findByReference(produit.getReference()).ifPresent(existant -> {
            throw new ReferenceProduitDejaUtiliseeException(produit.getReference());
        });
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
        if (ligneCommandeRepository.existsByProduitId(id)) {
            throw new ProduitReferenceParCommandeException(id);
        }
        produitRepository.delete(existant);
        log.info("produit supprime, id={}", id);
    }

    public List<Produit> listerStockFaible(int seuil) {
        return produitRepository.findByQuantiteStockLessThanEqual(seuil);
    }

    public List<Produit> listerEnDessousDeLeurSeuilAlerte() {
        return produitRepository.findEnDessousDeLeurSeuilAlerte();
    }

    public List<ResumeStockCategorie> resumerStockParCategorie() {
        return produitRepository.resumerStockParCategorie();
    }

    public Produit reapprovisionner(Long id, int quantiteAjoutee) {
        if (quantiteAjoutee <= 0) {
            throw new IllegalArgumentException("la quantite reapprovisionnee doit etre superieure a zero");
        }
        Produit produit = recupererParId(id);
        produit.setQuantiteStock(produit.getQuantiteStock() + quantiteAjoutee);
        Produit maj = produitRepository.save(produit);
        log.info("reapprovisionnement, reference={}, quantite ajoutee={}, nouveau stock={}",
                maj.getReference(), quantiteAjoutee, maj.getQuantiteStock());
        return maj;
    }

    
}

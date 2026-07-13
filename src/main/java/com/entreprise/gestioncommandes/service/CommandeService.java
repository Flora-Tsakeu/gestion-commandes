package com.entreprise.gestioncommandes.service;

import com.entreprise.gestioncommandes.dto.CommandeRequest;
import com.entreprise.gestioncommandes.dto.LigneCommandeRequest;
import com.entreprise.gestioncommandes.exception.ProduitIntrouvableException;
import com.entreprise.gestioncommandes.exception.StockInsuffisantException;
import com.entreprise.gestioncommandes.model.Commande;
import com.entreprise.gestioncommandes.model.LigneCommande;
import com.entreprise.gestioncommandes.model.Produit;
import com.entreprise.gestioncommandes.repository.CommandeRepository;
import com.entreprise.gestioncommandes.repository.ProduitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CommandeService {

    private static final Logger log = LoggerFactory.getLogger(CommandeService.class);
      private static final BigDecimal TAUX_TVA = new BigDecimal("0.20");
    
    private final CommandeRepository commandeRepository;
    private final ProduitRepository produitRepository;

    public CommandeService(CommandeRepository commandeRepository, ProduitRepository produitRepository) {
        this.commandeRepository = commandeRepository;
        this.produitRepository = produitRepository;
    }

    @Transactional
    public Commande creerCommande(CommandeRequest requete) {
        Commande commande = new Commande(requete.getClient());
        BigDecimal totalHt = BigDecimal.ZERO;

        for (LigneCommandeRequest ligneRequete : requete.getLignes()) {
            Produit produit = produitRepository.findById(ligneRequete.getProduitId())
                    .orElseThrow(() -> new ProduitIntrouvableException(ligneRequete.getProduitId()));

            if (produit.getQuantiteStock() < ligneRequete.getQuantite()) {
                throw new StockInsuffisantException(produit.getReference(), ligneRequete.getQuantite(), produit.getQuantiteStock());
            }

            produit.setQuantiteStock(produit.getQuantiteStock() - ligneRequete.getQuantite());
            produitRepository.save(produit);

            LigneCommande ligne = new LigneCommande(produit, ligneRequete.getQuantite());
            ligne.setPrixUnitaireHtApplique(produit.getPrixUnitaireHt());
            ligne.setCommande(commande);
            commande.getLignes().add(ligne);

            totalHt = totalHt.add(produit.getPrixUnitaireHt().multiply(BigDecimal.valueOf(ligneRequete.getQuantite())));
        }

        commande.setMontantTotalHt(totalHt.setScale(2, RoundingMode.HALF_UP));
        BigDecimal totalTtc = totalHt.multiply(BigDecimal.ONE.add(TAUX_TVA));
        commande.setMontantTotalTtc(totalTtc.setScale(2, RoundingMode.HALF_UP));
        
        Commande enregistree = commandeRepository.save(commande);
        log.info("commande creee, client={}, nbLignes={}, totalTtc={}",
                enregistree.getClient(), enregistree.getLignes().size(), enregistree.getMontantTotalTtc());
        return enregistree;
    }

    public Commande recupererParId(Long id) {
        return commandeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("commande introuvable pour l'identifiant " + id));
    }
}

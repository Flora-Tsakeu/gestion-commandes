package com.entreprise.gestioncommandes.service;

import com.entreprise.gestioncommandes.dto.CommandeRequest;
import com.entreprise.gestioncommandes.dto.LigneCommandeRequest;
import com.entreprise.gestioncommandes.exception.CommandeIntrouvableException;
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
import java.util.List;

@Service
public class CommandeService {

    private static final Logger log = LoggerFactory.getLogger(CommandeService.class);
    
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

        commande.setMontantTotalHt(CalculateurTva.arrondirDeuxDecimales(totalHt));
        commande.setMontantTotalTtc(CalculateurTva.calculerMontantTtc(totalHt));
        
        Commande enregistree = commandeRepository.save(commande);
        log.info("commande creee, client={}, nbLignes={}, totalTtc={}",
                enregistree.getClient(), enregistree.getLignes().size(), enregistree.getMontantTotalTtc());
        return enregistree;
    }

    public Commande recupererParId(Long id) {
        return commandeRepository.findById(id)
                .orElseThrow(() -> new CommandeIntrouvableException(id));
    }
    
    public List<Commande> listerToutes() {
        return commandeRepository.findAllByOrderByDateCreationDesc();
    }

    public List<Commande> listerParClient(String client) {
        return commandeRepository.findByClientOrderByDateCreationDesc(client);
    }

    @Transactional
    public Commande annuler(Long id) {
        Commande commande = recupererParId(id);
        if (commande.isAnnulee()) {
            log.warn("tentative d'annulation d'une commande deja annulee, id={}", id);
            return commande;
        }

        for (LigneCommande ligne : commande.getLignes()) {
            Produit produit = ligne.getProduit();
            produit.setQuantiteStock(produit.getQuantiteStock() + ligne.getQuantite());
            produitRepository.save(produit);
        }

        commande.setAnnulee(true);
        Commande misAJour = commandeRepository.save(commande);
        log.info("commande annulee, id={}, stock reintegre pour {} ligne(s)", id, commande.getLignes().size());
        return misAJour;
    }
}



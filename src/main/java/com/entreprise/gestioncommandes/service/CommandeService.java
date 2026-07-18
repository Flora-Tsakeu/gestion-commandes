package com.entreprise.gestioncommandes.service;

import com.entreprise.gestioncommandes.dto.CommandeRequest;
import com.entreprise.gestioncommandes.dto.LigneCommandeRequest;
import com.entreprise.gestioncommandes.dto.StatistiquesCommandes;
import com.entreprise.gestioncommandes.exception.AnnulationImpossibleException;
import com.entreprise.gestioncommandes.exception.CommandeIntrouvableException;
import com.entreprise.gestioncommandes.exception.MontantMinimumNonAtteintException;
import com.entreprise.gestioncommandes.exception.ProduitInactifException;
import com.entreprise.gestioncommandes.exception.ProduitIntrouvableException;
import com.entreprise.gestioncommandes.exception.ReferenceExterneDejaUtiliseeException;
import com.entreprise.gestioncommandes.exception.StockInsuffisantException;
import com.entreprise.gestioncommandes.model.Commande;
import com.entreprise.gestioncommandes.model.LigneCommande;
import com.entreprise.gestioncommandes.model.ModeLivraison;
import com.entreprise.gestioncommandes.model.Produit;
import com.entreprise.gestioncommandes.repository.CommandeRepository;
import com.entreprise.gestioncommandes.repository.ProduitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;


@Service
public class CommandeService {

    private static final Logger log = LoggerFactory.getLogger(CommandeService.class);
    private static final int DELAI_MAX_ANNULATION_JOURS = 30;
    private static final BigDecimal MONTANT_MINIMUM_HT = new BigDecimal("5.00");
    
    private final CommandeRepository commandeRepository;
    private final ProduitRepository produitRepository;
    private final NotificationService notificationService;

    public CommandeService(CommandeRepository commandeRepository, ProduitRepository produitRepository,
                            NotificationService notificationService) {
        this.commandeRepository = commandeRepository;
        this.produitRepository = produitRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public Commande creerCommande(CommandeRequest requete) {
        if (requete.getReferenceExterne() != null && !requete.getReferenceExterne().isBlank()
                && commandeRepository.existsByReferenceExterne(requete.getReferenceExterne())) {
            throw new ReferenceExterneDejaUtiliseeException(requete.getReferenceExterne());
        }
        
        Commande commande = new Commande(requete.getClient());
        BigDecimal totalHt = BigDecimal.ZERO;

        for (LigneCommandeRequest ligneRequete : requete.getLignes()) {
            Produit produit = produitRepository.findById(ligneRequete.getProduitId())
                    .orElseThrow(() -> new ProduitIntrouvableException(ligneRequete.getProduitId()));
            
            if (!produit.isActif()) {
                throw new ProduitInactifException(produit.getReference());
            }

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
        if (totalHt.compareTo(MONTANT_MINIMUM_HT) < 0) {
            throw new MontantMinimumNonAtteintException(CalculateurTva.arrondirDeuxDecimales(totalHt), MONTANT_MINIMUM_HT);
        }
        ModeLivraison modeLivraison = requete.getModeLivraison() != null ? requete.getModeLivraison() : ModeLivraison.STANDARD;
        commande.setModeLivraison(modeLivraison);
        BigDecimal totalTtcAvecLivraison = CalculateurTva.calculerMontantTtc(totalHt).add(modeLivraison.getFraisHt());
        commande.setMontantTotalTtc(CalculateurTva.arrondirDeuxDecimales(totalTtcAvecLivraison));
        commande.setNumeroSuivi(GenerateurNumeroSuivi.genererNouveauNumero());
        commande.setNotes(requete.getNotes());
        commande.setClientEmail(requete.getClientEmail());
        commande.setReferenceExterne(requete.getReferenceExterne());

        
        Commande enregistree = commandeRepository.save(commande);
        log.info("commande creee, client={}, nbLignes={}, totalTtc={}",
                enregistree.getClient(), enregistree.getLignes().size(), enregistree.getMontantTotalTtc());
        notificationService.notifierCreation(enregistree);
        return enregistree;
    }

    public Commande recupererParId(Long id) {
        return commandeRepository.findById(id)
                .orElseThrow(() -> new CommandeIntrouvableException(id));
    }

    public Commande recupererParNumeroSuivi(String numeroSuivi) {
        return commandeRepository.findByNumeroSuivi(numeroSuivi)
                .orElseThrow(() -> new CommandeIntrouvableException(numeroSuivi));
    }
    
    public List<Commande> listerToutes() {
        return commandeRepository.findAllByOrderByDateCreationDesc();
    }

    public List<Commande> listerParClient(String client) {
        return commandeRepository.findByClientOrderByDateCreationDesc(client);
    }

    public Page<Commande> listerToutesPagine(Pageable pageable) {
        return commandeRepository.findAllByOrderByDateCreationDesc(pageable);
    }

    public Page<Commande> listerParClientPagine(String client, Pageable pageable) {
        return commandeRepository.findByClientOrderByDateCreationDesc(client, pageable);
    }

    public List<Commande> listerParStatutAnnulation(boolean annulee) {
        return commandeRepository.findByAnnuleeOrderByDateCreationDesc(annulee);
    }

    public List<Commande> listerParPeriode(LocalDateTime debut, LocalDateTime fin) {
        return commandeRepository.findByDateCreationBetweenOrderByDateCreationDesc(debut, fin);
    }

    public StatistiquesCommandes calculerStatistiques() {
        long actives = commandeRepository.countByAnnulee(false);
        long annulees = commandeRepository.countByAnnulee(true);
        BigDecimal chiffreAffaires = commandeRepository.calculerChiffreAffairesActif();
        return new StatistiquesCommandes(actives, annulees, chiffreAffaires);
    }

    public Commande modifierNotes(Long id, String notes) {
        Commande commande = recupererParId(id);
        commande.setNotes(notes);
        Commande maj = commandeRepository.save(commande);
        log.info("notes mises a jour, commande id={}", id);
        return maj;
    }

    @Transactional
    public Commande annuler(Long id) {
        Commande commande = recupererParId(id);
        if (commande.isAnnulee()) {
            log.warn("tentative d'annulation d'une commande deja annulee, id={}", id);
            return commande;
        }

        long joursEcoules = ChronoUnit.DAYS.between(commande.getDateCreation(), LocalDateTime.now());
        if (joursEcoules > DELAI_MAX_ANNULATION_JOURS) {
            throw new AnnulationImpossibleException(id, DELAI_MAX_ANNULATION_JOURS);
        }

        for (LigneCommande ligne : commande.getLignes()) {
            Produit produit = ligne.getProduit();
            produit.setQuantiteStock(produit.getQuantiteStock() + ligne.getQuantite());
            produitRepository.save(produit);
        }

        commande.setAnnulee(true);
        Commande misAJour = commandeRepository.save(commande);
        log.info("commande annulee, id={}, stock reintegre pour {} ligne(s)", id, commande.getLignes().size());
        notificationService.notifierAnnulation(misAJour);
        return misAJour;
    }
}



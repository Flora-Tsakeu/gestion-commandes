package com.entreprise.gestioncommandes.service;

import com.entreprise.gestioncommandes.model.Produit;
import com.entreprise.gestioncommandes.repository.ProduitRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExportProduitService {

    private static final String ENTETE = "reference;libelle;categorie;prixUnitaireHt;quantiteStock;seuilAlerte";

    private final ProduitRepository produitRepository;

    public ExportProduitService(ProduitRepository produitRepository) {
        this.produitRepository = produitRepository;
    }

    public String exporterCatalogueEnCsv() {
        List<Produit> produits = produitRepository.findAll();

        String lignes = produits.stream()
                .map(this::formaterLigne)
                .collect(Collectors.joining("\n"));

        return ENTETE + "\n" + lignes;
    }

    private String formaterLigne(Produit produit) {
        return String.join(";",
                nettoyer(produit.getReference()),
                nettoyer(produit.getLibelle()),
                nettoyer(produit.getCategorie()),
                String.valueOf(produit.getPrixUnitaireHt()),
                String.valueOf(produit.getQuantiteStock()),
                String.valueOf(produit.getSeuilAlerte()));
    }

    private String nettoyer(String valeur) {
        return valeur == null ? "" : valeur.replace(";", ",");
    }
}

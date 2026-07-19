package com.entreprise.gestioncommandes.service;

import com.entreprise.gestioncommandes.dto.LigneCommandeRequest;
import com.entreprise.gestioncommandes.model.LigneCommande;
import com.entreprise.gestioncommandes.model.Produit;

public final class CommandeMapper {

    private CommandeMapper() {
    }

    public static LigneCommande construireLigne(Produit produit, Integer quantite) {
        LigneCommande ligne = new LigneCommande(produit, quantite);
        ligne.setPrixUnitaireHtApplique(produit.getPrixUnitaireHt());
        return ligne;
    }

    public static LigneCommandeRequest versLigneCommandeRequest(LigneCommande ligne) {
        LigneCommandeRequest requete = new LigneCommandeRequest();
        requete.setProduitId(ligne.getProduit().getId());
        requete.setQuantite(ligne.getQuantite());
        return requete;
    }
}

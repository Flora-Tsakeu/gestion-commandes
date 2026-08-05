package com.entreprise.gestioncommandes.service;

import com.entreprise.gestioncommandes.exception.ProduitInactifException;
import com.entreprise.gestioncommandes.exception.StockInsuffisantException;
import com.entreprise.gestioncommandes.model.Produit;

public final class ValidateurCommande {

    private ValidateurCommande() {
    }

    public static void verifierProduitCommandable(Produit produit, int quantiteDemandee) {
        if (!produit.isActif()) {
            throw new ProduitInactifException(produit.getReference());
        }
        if (produit.getQuantiteStock() < quantiteDemandee) {
            throw new StockInsuffisantException(produit.getReference(), quantiteDemandee, produit.getQuantiteStock());
        }
    }
}

package com.entreprise.gestioncommandes.service;

import com.entreprise.gestioncommandes.dto.IntegriteCommande;
import com.entreprise.gestioncommandes.model.Commande;
import com.entreprise.gestioncommandes.model.LigneCommande;

import java.math.BigDecimal;

public final class IntegriteCommandeService {

    private IntegriteCommandeService() {
    }

    public static IntegriteCommande verifier(Commande commande) {
        BigDecimal totalRecalcule = commande.getLignes().stream()
                .map(LigneCommande::getMontantLigneHt)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        totalRecalcule = CalculateurTva.arrondirDeuxDecimales(totalRecalcule);

        boolean integre = totalRecalcule.compareTo(commande.getMontantTotalHt()) == 0;
        return new IntegriteCommande(integre, commande.getMontantTotalHt(), totalRecalcule);
    }
}

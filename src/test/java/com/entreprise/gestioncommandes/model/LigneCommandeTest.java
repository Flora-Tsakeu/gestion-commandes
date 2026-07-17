package com.entreprise.gestioncommandes.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class LigneCommandeTest {

    @Test
    void doitCalculerLeMontantDeLaLigneEnFonctionDuPrixEtDeLaQuantite() {
        Produit produit = new Produit("ECR-027", "Ecran 27 pouces", new BigDecimal("199.00"), 5);
        LigneCommande ligne = new LigneCommande(produit, 3);
        ligne.setPrixUnitaireHtApplique(new BigDecimal("199.00"));

        assertThat(ligne.getMontantLigneHt()).isEqualByComparingTo("597.00");
    }

    @Test
    void doitRenvoyerZeroSiLePrixAppliqueNestPasEncoreDefini() {
        Produit produit = new Produit("ECR-027", "Ecran 27 pouces", new BigDecimal("199.00"), 5);
        LigneCommande ligne = new LigneCommande(produit, 3);

        assertThat(ligne.getMontantLigneHt()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}

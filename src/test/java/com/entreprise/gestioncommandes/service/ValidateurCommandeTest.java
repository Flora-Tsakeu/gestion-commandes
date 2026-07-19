package com.entreprise.gestioncommandes.service;

import com.entreprise.gestioncommandes.exception.ProduitInactifException;
import com.entreprise.gestioncommandes.exception.StockInsuffisantException;
import com.entreprise.gestioncommandes.model.Produit;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValidateurCommandeTest {

    @Test
    void neLevePasDExceptionSiProduitActifEtStockSuffisant() {
        Produit produit = new Produit("ECR-027", "Ecran 27 pouces", new BigDecimal("199.00"), 5);

        assertThatCode(() -> ValidateurCommande.verifierProduitCommandable(produit, 3))
                .doesNotThrowAnyException();
    }

    @Test
    void leveUneExceptionSiProduitInactif() {
        Produit produit = new Produit("ECR-027", "Ecran 27 pouces", new BigDecimal("199.00"), 5);
        produit.setActif(false);

        assertThatThrownBy(() -> ValidateurCommande.verifierProduitCommandable(produit, 1))
                .isInstanceOf(ProduitInactifException.class)
                .hasMessageContaining("ECR-027");
    }

    @Test
    void leveUneExceptionSiStockInsuffisant() {
        Produit produit = new Produit("ECR-027", "Ecran 27 pouces", new BigDecimal("199.00"), 2);

        assertThatThrownBy(() -> ValidateurCommande.verifierProduitCommandable(produit, 5))
                .isInstanceOf(StockInsuffisantException.class)
                .hasMessageContaining("ECR-027");
    }
}

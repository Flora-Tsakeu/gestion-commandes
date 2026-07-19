package com.entreprise.gestioncommandes.service;

import com.entreprise.gestioncommandes.dto.LigneCommandeRequest;
import com.entreprise.gestioncommandes.model.LigneCommande;
import com.entreprise.gestioncommandes.model.Produit;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CommandeMapperTest {

    @Test
    void doitConstruireUneLigneAvecLePrixDuProduitFigee() {
        Produit produit = new Produit("ECR-027", "Ecran 27 pouces", new BigDecimal("199.00"), 5);
        produit.setId(2L);

        LigneCommande ligne = CommandeMapper.construireLigne(produit, 3);

        assertThat(ligne.getProduit()).isEqualTo(produit);
        assertThat(ligne.getQuantite()).isEqualTo(3);
        assertThat(ligne.getPrixUnitaireHtApplique()).isEqualByComparingTo("199.00");
    }

    @Test
    void doitConvertirUneLigneExistanteEnRequeteDeDuplication() {
        Produit produit = new Produit("ECR-027", "Ecran 27 pouces", new BigDecimal("199.00"), 5);
        produit.setId(2L);
        LigneCommande ligne = new LigneCommande(produit, 4);

        LigneCommandeRequest requete = CommandeMapper.versLigneCommandeRequest(ligne);

        assertThat(requete.getProduitId()).isEqualTo(2L);
        assertThat(requete.getQuantite()).isEqualTo(4);
    }
}

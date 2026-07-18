package com.entreprise.gestioncommandes.service;

import com.entreprise.gestioncommandes.dto.IntegriteCommande;
import com.entreprise.gestioncommandes.model.Commande;
import com.entreprise.gestioncommandes.model.LigneCommande;
import com.entreprise.gestioncommandes.model.Produit;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class IntegriteCommandeServiceTest {

    @Test
    void doitDeclarerIntegreSiLaSommeDesLignesCorrespondAuTotal() {
        Produit ecran = new Produit("ECR-027", "Ecran 27 pouces", new BigDecimal("199.00"), 5);
        LigneCommande ligne = new LigneCommande(ecran, 2);
        ligne.setPrixUnitaireHtApplique(new BigDecimal("199.00"));

        Commande commande = new Commande("Societe Dubois");
        commande.setLignes(List.of(ligne));
        commande.setMontantTotalHt(new BigDecimal("398.00"));

        IntegriteCommande resultat = IntegriteCommandeService.verifier(commande);

        assertThat(resultat.isIntegre()).isTrue();
        assertThat(resultat.getMontantTotalHtRecalcule()).isEqualByComparingTo("398.00");
    }

    @Test
    void doitDeclarerNonIntegreSiLeTotalDeclareEstIncoherent() {
        Produit ecran = new Produit("ECR-027", "Ecran 27 pouces", new BigDecimal("199.00"), 5);
        LigneCommande ligne = new LigneCommande(ecran, 2);
        ligne.setPrixUnitaireHtApplique(new BigDecimal("199.00"));

        Commande commande = new Commande("Societe Dubois");
        commande.setLignes(List.of(ligne));
        commande.setMontantTotalHt(new BigDecimal("300.00"));

        IntegriteCommande resultat = IntegriteCommandeService.verifier(commande);

        assertThat(resultat.isIntegre()).isFalse();
        assertThat(resultat.getMontantTotalHtDeclare()).isEqualByComparingTo("300.00");
        assertThat(resultat.getMontantTotalHtRecalcule()).isEqualByComparingTo("398.00");
    }
}

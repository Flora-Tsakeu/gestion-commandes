package com.entreprise.gestioncommandes.service;

import com.entreprise.gestioncommandes.dto.ResumeQuotidien;
import com.entreprise.gestioncommandes.model.Commande;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ResumeQuotidienServiceTest {

    @Test
    void doitRegrouperLesCommandesActivesParJour() {
        Commande premiere = new Commande("Boutique Nord");
        premiere.setDateCreation(LocalDateTime.of(2026, 7, 10, 9, 0));
        premiere.setMontantTotalTtc(new BigDecimal("100.00"));

        Commande deuxieme = new Commande("Boutique Sud");
        deuxieme.setDateCreation(LocalDateTime.of(2026, 7, 10, 15, 0));
        deuxieme.setMontantTotalTtc(new BigDecimal("50.00"));

        Commande troisieme = new Commande("Boutique Est");
        troisieme.setDateCreation(LocalDateTime.of(2026, 7, 11, 9, 0));
        troisieme.setMontantTotalTtc(new BigDecimal("75.00"));

        List<ResumeQuotidien> resultat = ResumeQuotidienService.regrouperParJour(List.of(premiere, deuxieme, troisieme));

        assertThat(resultat).hasSize(2);
        assertThat(resultat.get(0).getNombreCommandes()).isEqualTo(2);
        assertThat(resultat.get(0).getChiffreAffairesTtc()).isEqualByComparingTo("150.00");
        assertThat(resultat.get(1).getNombreCommandes()).isEqualTo(1);
    }

    @Test
    void doitExclureLesCommandesAnnuleesDuResume() {
        Commande annulee = new Commande("Boutique Nord");
        annulee.setDateCreation(LocalDateTime.of(2026, 7, 10, 9, 0));
        annulee.setMontantTotalTtc(new BigDecimal("100.00"));
        annulee.setAnnulee(true);

        List<ResumeQuotidien> resultat = ResumeQuotidienService.regrouperParJour(List.of(annulee));

        assertThat(resultat).isEmpty();
    }
}

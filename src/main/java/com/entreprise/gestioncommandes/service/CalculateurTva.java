package com.entreprise.gestioncommandes.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class CalculateurTva {

    private static final BigDecimal TAUX_TVA = new BigDecimal("0.20");

    private CalculateurTva() {
    }

    public static BigDecimal arrondirDeuxDecimales(BigDecimal montant) {
        return montant.setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal calculerMontantTtc(BigDecimal montantHt) {
        return arrondirDeuxDecimales(montantHt.multiply(BigDecimal.ONE.add(TAUX_TVA)));
    }
}

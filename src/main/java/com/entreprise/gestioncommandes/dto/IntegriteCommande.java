package com.entreprise.gestioncommandes.dto;

import java.math.BigDecimal;

public class IntegriteCommande {

    private final boolean integre;
    private final BigDecimal montantTotalHtDeclare;
    private final BigDecimal montantTotalHtRecalcule;

    public IntegriteCommande(boolean integre, BigDecimal montantTotalHtDeclare, BigDecimal montantTotalHtRecalcule) {
        this.integre = integre;
        this.montantTotalHtDeclare = montantTotalHtDeclare;
        this.montantTotalHtRecalcule = montantTotalHtRecalcule;
    }

    public boolean isIntegre() {
        return integre;
    }

    public BigDecimal getMontantTotalHtDeclare() {
        return montantTotalHtDeclare;
    }

    public BigDecimal getMontantTotalHtRecalcule() {
        return montantTotalHtRecalcule;
    }
}

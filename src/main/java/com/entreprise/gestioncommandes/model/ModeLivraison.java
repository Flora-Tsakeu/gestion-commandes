package com.entreprise.gestioncommandes.model;

import java.math.BigDecimal;

public enum ModeLivraison {

    STANDARD(BigDecimal.ZERO),
    EXPRESS(new BigDecimal("9.90"));

    private final BigDecimal fraisHt;

    ModeLivraison(BigDecimal fraisHt) {
        this.fraisHt = fraisHt;
    }

    public BigDecimal getFraisHt() {
        return fraisHt;
    }
}

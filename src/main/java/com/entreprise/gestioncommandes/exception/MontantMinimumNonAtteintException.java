package com.entreprise.gestioncommandes.exception;

import java.math.BigDecimal;

public class MontantMinimumNonAtteintException extends RuntimeException {

    public MontantMinimumNonAtteintException(BigDecimal montantHt, BigDecimal minimumRequis) {
        super("le montant de la commande (" + montantHt + " HT) est inferieur au minimum requis de " + minimumRequis + " HT");
    }
}

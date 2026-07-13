package com.entreprise.gestioncommandes.exception;

public class StockInsuffisantException extends RuntimeException {

    public StockInsuffisantException(String reference, int demande, int disponible) {
        super("stock insuffisant pour " + reference + " : demande=" + demande + ", disponible=" + disponible);
    }
}

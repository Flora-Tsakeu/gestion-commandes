package com.entreprise.gestioncommandes.exception;

public class ProduitInactifException extends RuntimeException {

    public ProduitInactifException(String reference) {
        super("le produit " + reference + " est desactive et ne peut plus etre commande");
    }
}

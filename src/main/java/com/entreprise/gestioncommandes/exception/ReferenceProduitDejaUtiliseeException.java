package com.entreprise.gestioncommandes.exception;

public class ReferenceProduitDejaUtiliseeException extends RuntimeException {

    public ReferenceProduitDejaUtiliseeException(String reference) {
        super("la reference " + reference + " est deja attribuee a un autre produit");
    }
}

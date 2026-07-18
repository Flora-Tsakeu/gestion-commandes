package com.entreprise.gestioncommandes.exception;

public class ReferenceExterneDejaUtiliseeException extends RuntimeException {

    public ReferenceExterneDejaUtiliseeException(String referenceExterne) {
        super("la reference externe " + referenceExterne + " est deja associee a une autre commande");
    }
}

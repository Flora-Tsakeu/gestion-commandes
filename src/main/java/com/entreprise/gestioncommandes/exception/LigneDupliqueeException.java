package com.entreprise.gestioncommandes.exception;

public class LigneDupliqueeException extends RuntimeException {

    public LigneDupliqueeException(Long produitId) {
        super("le produit " + produitId + " apparait plusieurs fois dans les lignes de la commande");
    }
}

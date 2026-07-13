package com.entreprise.gestioncommandes.exception;

public class ProduitIntrouvableException extends RuntimeException {

    public ProduitIntrouvableException(Long id) {
        super("aucun produit trouve pour l'identifiant " + id);
    }

    public ProduitIntrouvableException(String reference) {
        super("aucun produit trouve pour la reference " + reference);
    }
}

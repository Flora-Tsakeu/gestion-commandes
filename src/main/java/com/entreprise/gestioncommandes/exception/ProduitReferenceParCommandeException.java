package com.entreprise.gestioncommandes.exception;

public class ProduitReferenceParCommandeException extends RuntimeException {

    public ProduitReferenceParCommandeException(Long idProduit) {
        super("le produit " + idProduit + " ne peut pas etre supprime car il est reference dans au moins une commande");
    }
}

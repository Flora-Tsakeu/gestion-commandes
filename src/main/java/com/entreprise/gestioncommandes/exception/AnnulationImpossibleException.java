package com.entreprise.gestioncommandes.exception;

public class AnnulationImpossibleException extends RuntimeException {

    public AnnulationImpossibleException(Long idCommande, int delaiMaxJours) {
        super("la commande " + idCommande + " ne peut plus etre annulee au dela de " + delaiMaxJours + " jours");
    }
}

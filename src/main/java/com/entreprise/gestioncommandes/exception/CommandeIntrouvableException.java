package com.entreprise.gestioncommandes.exception;

public class CommandeIntrouvableException extends RuntimeException {

    public CommandeIntrouvableException(Long id) {
        super("aucune commande trouvee pour l'identifiant " + id);
    }
}

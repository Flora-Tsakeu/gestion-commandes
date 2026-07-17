package com.entreprise.gestioncommandes.exception;

public class CommandeIntrouvableException extends RuntimeException {

    public CommandeIntrouvableException(Long id) {
        super("aucune commande trouvee pour l'identifiant " + id);
    }

    public CommandeIntrouvableException(String numeroSuivi) {
        super("aucune commande trouvee pour le numero de suivi " + numeroSuivi);
    }
}

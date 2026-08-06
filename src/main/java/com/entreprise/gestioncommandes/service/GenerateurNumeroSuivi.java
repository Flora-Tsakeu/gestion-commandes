package com.entreprise.gestioncommandes.service;

import java.util.UUID;

public final class GenerateurNumeroSuivi {

    private static final String PREFIXE = "CMD-";
    private static final int LONGUEUR_SUFFIXE = 12;

    private GenerateurNumeroSuivi() {
    }

    public static String genererNouveauNumero() {
        UUID nouveauNumero = UUID.randomUUID();
        return PREFIXE + nouveauNumero.substring(0, LONGUEUR_SUFFIXE).toUpperCase();
    }
}

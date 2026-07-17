package com.entreprise.gestioncommandes.service;

import java.util.UUID;

public final class GenerateurNumeroSuivi {

    private static final String PREFIXE = "CMD-";
    private static final int LONGUEUR_SUFFIXE = 8;

    private GenerateurNumeroSuivi() {
    }

    public static String genererNouveauNumero() {
        return PREFIXE + UUID.randomUUID().toString().substring(0, LONGUEUR_SUFFIXE).toUpperCase();
    }
}

package com.entreprise.gestioncommandes.dto;

public class TopProduitVendu {

    private final String reference;
    private final String libelle;
    private final Long quantiteTotaleVendue;

    public TopProduitVendu(String reference, String libelle, Long quantiteTotaleVendue) {
        this.reference = reference;
        this.libelle = libelle;
        this.quantiteTotaleVendue = quantiteTotaleVendue;
    }

    public String getReference() {
        return reference;
    }

    public String getLibelle() {
        return libelle;
    }

    public Long getQuantiteTotaleVendue() {
        return quantiteTotaleVendue;
    }
}

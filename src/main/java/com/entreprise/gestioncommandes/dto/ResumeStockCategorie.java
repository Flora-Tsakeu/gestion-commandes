package com.entreprise.gestioncommandes.dto;

import java.math.BigDecimal;

public class ResumeStockCategorie {

    private final String categorie;
    private final Long nombreProduits;
    private final BigDecimal valeurStockTotaleHt;

    public ResumeStockCategorie(String categorie, Long nombreProduits, BigDecimal valeurStockTotaleHt) {
        this.categorie = categorie;
        this.nombreProduits = nombreProduits;
        this.valeurStockTotaleHt = valeurStockTotaleHt;
    }

    public String getCategorie() {
        return categorie;
    }

    public Long getNombreProduits() {
        return nombreProduits;
    }

    public BigDecimal getValeurStockTotaleHt() {
        return valeurStockTotaleHt;
    }
}

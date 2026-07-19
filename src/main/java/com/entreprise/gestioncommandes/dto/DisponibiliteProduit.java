package com.entreprise.gestioncommandes.dto;

public class DisponibiliteProduit {

    private final boolean disponible;
    private final Integer quantiteStock;
    private final Integer quantiteDemandee;

    public DisponibiliteProduit(boolean disponible, Integer quantiteStock, Integer quantiteDemandee) {
        this.disponible = disponible;
        this.quantiteStock = quantiteStock;
        this.quantiteDemandee = quantiteDemandee;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public Integer getQuantiteStock() {
        return quantiteStock;
    }

    public Integer getQuantiteDemandee() {
        return quantiteDemandee;
    }
}

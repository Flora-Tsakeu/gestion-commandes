package com.entreprise.gestioncommandes.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ReapprovisionnementRequest {

    @NotNull
    @Min(value = 1, message = "la quantite reapprovisionnee doit etre superieure a zero")
    private Integer quantite;

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }
}

package com.entreprise.gestioncommandes.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class LigneCommandeRequest {

    @NotNull(message = "l'identifiant du produit est obligatoire")
    private Long produitId;

    @NotNull
    @Min(value = 1, message = "la quantite doit etre superieure a zero")
    @Max(value = 100, message = "la quantite ne peut pas depasser 100 unites par ligne")
    private Integer quantite;

    public Long getProduitId() {
        return produitId;
    }

    public void setProduitId(Long produitId) {
        this.produitId = produitId;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }
}

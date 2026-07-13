package com.entreprise.gestioncommandes.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class CommandeRequest {

    @NotBlank(message = "le nom du client est obligatoire")
    private String client;

    @NotEmpty(message = "une commande doit contenir au moins une ligne")
    @Valid
    private List<LigneCommandeRequest> lignes;

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public List<LigneCommandeRequest> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneCommandeRequest> lignes) {
        this.lignes = lignes;
    }
}

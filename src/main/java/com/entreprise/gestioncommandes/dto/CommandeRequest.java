package com.entreprise.gestioncommandes.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

import com.entreprise.gestioncommandes.model.ModeLivraison;

public class CommandeRequest {

    @NotBlank(message = "le nom du client est obligatoire")
    private String client;

    @NotEmpty(message = "une commande doit contenir au moins une ligne")
    @Valid
    private List<LigneCommandeRequest> lignes;

    @Size(max = 500, message = "les notes ne peuvent pas depasser 500 caracteres")
    private String notes;

    private ModeLivraison modeLivraison;

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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public ModeLivraison getModeLivraison() {
        return modeLivraison;
    }

    public void setModeLivraison(ModeLivraison modeLivraison) {
        this.modeLivraison = modeLivraison;
    }
}

package com.entreprise.gestioncommandes.dto;

import java.time.LocalDateTime;

public class InfoApi {

    private final String nomApplication;
    private final String version;
    private final LocalDateTime horodatageServeur;

    public InfoApi(String nomApplication, String version, LocalDateTime horodatageServeur) {
        this.nomApplication = nomApplication;
        this.version = version;
        this.horodatageServeur = horodatageServeur;
    }

    public String getNomApplication() {
        return nomApplication;
    }

    public String getVersion() {
        return version;
    }

    public LocalDateTime getHorodatageServeur() {
        return horodatageServeur;
    }
}
